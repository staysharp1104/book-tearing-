package com.webbook.service;

import com.webbook.config.DeepSeekConfig;
import com.webbook.dto.ChatRequestDTO;
import com.webbook.dto.SourceRecordDTO;
import com.webbook.entity.ChatHistory;
import com.webbook.entity.PromptTemplate;
import com.webbook.entity.RagSourceRecord;
import com.webbook.rag.*;
import com.webbook.repository.ChatHistoryRepository;
import com.webbook.repository.PromptTemplateRepository;
import com.webbook.repository.RagSourceRecordRepository;
import com.webbook.repository.RagIndexInfoRepository;
import com.webbook.entity.RagIndexInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final DeepSeekConfig deepSeekConfig;
    private final ChatHistoryRepository chatHistoryRepository;
    private final RagIndexInfoRepository ragIndexInfoRepository;
    private final RagSourceRecordRepository ragSourceRecordRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final RAGService ragService;
    private final RAGCacheManager cacheManager;

    public ChatService(DeepSeekConfig deepSeekConfig,
                       ChatHistoryRepository chatHistoryRepository,
                       RagIndexInfoRepository ragIndexInfoRepository,
                       RagSourceRecordRepository ragSourceRecordRepository,
                       PromptTemplateRepository promptTemplateRepository,
                       RAGService ragService,
                       RAGCacheManager cacheManager) {
        this.deepSeekConfig = deepSeekConfig;
        this.chatHistoryRepository = chatHistoryRepository;
        this.ragIndexInfoRepository = ragIndexInfoRepository;
        this.ragSourceRecordRepository = ragSourceRecordRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.ragService = ragService;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public SseEmitter streamChat(ChatRequestDTO request) {
        String bookId = request.getBookId();
        String userMessage = request.getMessage();
        boolean tenChapterContext = Boolean.TRUE.equals(request.getTenChapterContext());

        SseEmitter emitter = new SseEmitter(180000L);

        ChatHistory userMsg = new ChatHistory();
        userMsg.setBookId(bookId);
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        userMsg.setCreateTime(LocalDateTime.now());
        userMsg.setSource("fanqie");
        chatHistoryRepository.save(userMsg);

        List<ChatHistory> history = chatHistoryRepository.findByBookIdOrderByCreateTimeAsc(bookId);

        String context = buildContext(bookId, userMessage, tenChapterContext, request.getPromptTemplateId());

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "\u4f60\u662f\u4e00\u4e2a\u4e13\u4e1a\u7684\u5c0f\u8bf4\u62c6\u4e66\u5206\u6790\u52a9\u624b\uff0c\u5e2e\u52a9\u7528\u6237\u5206\u6790\u5c0f\u8bf4\u7ed3\u6784\u3001\u5267\u60c5\u3001\u4eba\u7269\u7b49\u3002"));

        for (ChatHistory msg : history) {
            if (msg.getRole().equals("user") || msg.getRole().equals("assistant")) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }

        String fullMessage = context.isEmpty() ? userMessage : context + "\n\n\u7528\u6237\u95ee\u9898: " + userMessage;
        messages.remove(messages.size() - 1);
        messages.add(Map.of("role", "user", "content", fullMessage));

        String apiKey = deepSeekConfig.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("{\"error\":\"API Key \u672a\u914d\u7f6e\"}"));
                emitter.complete();
            } catch (IOException e) {}
            return emitter;
        }

        String finalContext = context;
        final Long[] savedAssistantMsgId = new Long[1];

        new Thread(() -> {
            try {
                HttpURLConnection conn = buildDeepSeekConnection(messages);
                int responseCode = conn.getResponseCode();

                if (responseCode != 200) {
                    String errorBody = readStream(conn.getErrorStream());
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"API\u8c03\u7528\u5931\u8d25: " + errorBody + "\"}"));
                    emitter.complete();
                    return;
                }

                ChatHistory assistantMsg = new ChatHistory();
                assistantMsg.setBookId(bookId);
                assistantMsg.setRole("assistant");
                assistantMsg.setContent("");
                assistantMsg.setCreateTime(LocalDateTime.now());
                assistantMsg.setSource("fanqie");
                chatHistoryRepository.save(assistantMsg);
                savedAssistantMsgId[0] = assistantMsg.getId();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder fullResponse = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) break;

                        String content = parseDeltaContent(data);
                        if (content != null && !content.isEmpty()) {
                            fullResponse.append(content);
                            String jsonContent = content.replace("\\", "\\\\")
                                    .replace("\"", "\\\"")
                                    .replace("\n", "\\n")
                                    .replace("\r", "\\r")
                                    .replace("\t", "\\t");
                            emitter.send(SseEmitter.event().name("token").data("{\"token\":\"" + jsonContent + "\"}"));
                        }
                    }
                }

                assistantMsg.setContent(fullResponse.toString());
                chatHistoryRepository.save(assistantMsg);

                boolean hasRagIndex = ragIndexInfoRepository.findById(bookId)
                        .map(info -> "built".equals(info.getStatus()))
                        .orElse(false);

                if (hasRagIndex && !fullResponse.isEmpty()) {
                    List<SourceRecordDTO> sources = ragService.searchWithSources(bookId, userMessage);
                    if (sources != null && !sources.isEmpty()) {
                        for (SourceRecordDTO src : sources) {
                            RagSourceRecord record = new RagSourceRecord();
                            record.setChatMessageId(savedAssistantMsgId[0]);
                            record.setBookId(bookId);
                            record.setChapterIndex(src.getChapterIndex());
                            record.setChapterTitle(src.getChapterTitle());
                            record.setExcerpt(src.getExcerpt());
                            record.setRank(src.getRank());
                            record.setCreatedAt(LocalDateTime.now());
                            ragSourceRecordRepository.save(record);
                        }

                        StringBuilder sourcesJson = new StringBuilder("[");
                        for (int i = 0; i < sources.size(); i++) {
                            if (i > 0) sourcesJson.append(",");
                            SourceRecordDTO s = sources.get(i);
                            sourcesJson.append("{")
                                    .append("\"chapterIndex\":").append(s.getChapterIndex()).append(",")
                                    .append("\"chapterTitle\":\"").append(escapeJson(s.getChapterTitle())).append("\",")
                                    .append("\"excerpt\":\"").append(escapeJson(s.getExcerpt())).append("\",")
                                    .append("\"rank\":").append(s.getRank())
                                    .append("}");
                        }
                        sourcesJson.append("]");
                        emitter.send(SseEmitter.event().name("sources").data("{\"sources\":" + sourcesJson + "}"));
                    }
                }

                emitter.send(SseEmitter.event().name("done").data("{\"finish\":\"OK\"}"));
                emitter.complete();

            } catch (Exception e) {
                log.error("SSE streaming error", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}"));
                } catch (IOException ex) {}
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    private String buildContext(String bookId, String userMessage, boolean tenChapterContext, Integer promptTemplateId) {
        StringBuilder context = new StringBuilder();

        if (promptTemplateId != null) {
            Optional<PromptTemplate> template = promptTemplateRepository.findById(promptTemplateId);
            template.ifPresent(t -> context.append("\u3010\u62c6\u4e66\u6307\u4ee4\u3011\n").append(t.getContent()).append("\n\n"));
        }

        if (tenChapterContext) {
            try {
                var chapters = ragService.getTenChapterContext(bookId);
                if (chapters != null && !chapters.isEmpty()) {
                    context.append("\u3010\u5f53\u524d\u4e66\u7c4d\u524d\u5341\u7ae0\u5185\u5bb9\u3011\n");
                    for (var ch : chapters) {
                        context.append(ch.getTitle()).append("\n").append(ch.getContent()).append("\n\n");
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get ten chapter context for bookId: {}", bookId, e);
            }
        }

        return context.toString();
    }

    private HttpURLConnection buildDeepSeekConnection(List<Map<String, String>> messages) throws IOException {
        URL url = URI.create(deepSeekConfig.getApiUrl()).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        StringBuilder body = new StringBuilder();
        body.append("{\"model\":\"").append(deepSeekConfig.getModel()).append("\",");
        body.append("\"temperature\":").append(deepSeekConfig.getTemperature()).append(",");
        body.append("\"max_tokens\":").append(deepSeekConfig.getMaxTokens()).append(",");
        body.append("\"stream\":true,");
        body.append("\"messages\":[");

        for (int i = 0; i < messages.size(); i++) {
            if (i > 0) body.append(",");
            Map<String, String> msg = messages.get(i);
            body.append("{\"role\":\"").append(msg.get("role")).append("\",");
            body.append("\"content\":\"").append(escapeJson(msg.get("content"))).append("\"}");
        }
        body.append("]}");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return conn;
    }

    private String parseDeltaContent(String sseData) {
        try {
            JsonObject root = JsonParser.parseString(sseData).getAsJsonObject();
            var choices = root.getAsJsonArray("choices");
            if (choices != null && choices.size() > 0) {
                JsonObject delta = choices.get(0).getAsJsonObject()
                        .getAsJsonObject("delta");
                if (delta != null && delta.has("content")) {
                    String content = delta.get("content").getAsString();
                    return content.isEmpty() ? null : content;
                }
            }
        } catch (Exception e) {}
        return null;
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private List<RAGIndexer> getRagIndexers() {
        return Collections.emptyList();
    }

    public List<ChatHistory> getHistory(String bookId) {
        return chatHistoryRepository.findByBookIdOrderByCreateTimeAsc(bookId);
    }

    @Transactional
    public void clearHistory(String bookId) {
        chatHistoryRepository.deleteByBookId(bookId);
    }
}
