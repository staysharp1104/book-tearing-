package com.webbook.controller;

import com.webbook.dto.ChatRequestDTO;
import com.webbook.entity.ChatHistory;
import com.webbook.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequestDTO request) {
        return chatService.streamChat(request);
    }

    @GetMapping("/history/{bookId}")
    public ResponseEntity<List<ChatHistory>> getHistory(@PathVariable String bookId) {
        return ResponseEntity.ok(chatService.getHistory(bookId));
    }

    @DeleteMapping("/history/{bookId}")
    public ResponseEntity<Void> clearHistory(@PathVariable String bookId) {
        chatService.clearHistory(bookId);
        return ResponseEntity.ok().build();
    }
}
