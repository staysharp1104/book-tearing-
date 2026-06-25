package com.webbook.service;

import com.webbook.dto.AnalysisPageDTO;
import com.webbook.dto.AnalysisPageDTO.BookDTO;
import com.webbook.dto.AnalysisPageDTO.ChapterDTO;
import com.webbook.dto.PromptTemplateDTO;
import com.webbook.dto.RagStatusDTO;
import com.webbook.entity.Book;
import com.webbook.entity.Chapter;
import com.webbook.entity.PromptTemplate;
import com.webbook.repository.ChapterRepository;
import com.webbook.repository.PromptTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnalysisPageService {

    private final BookService bookService;
    private final ChapterService chapterService;
    private final RagIndexService ragIndexService;
    private final PromptTemplateService promptTemplateService;

    public AnalysisPageService(BookService bookService,
                               ChapterService chapterService,
                               RagIndexService ragIndexService,
                               PromptTemplateService promptTemplateService) {
        this.bookService = bookService;
        this.chapterService = chapterService;
        this.ragIndexService = ragIndexService;
        this.promptTemplateService = promptTemplateService;
    }

    public AnalysisPageDTO getAnalysisPage(String bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("\u4e66\u7c4d\u4e0d\u5b58\u5728: " + bookId));

        // Build book info
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(book.getBookId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setSource(book.getSource());
        bookDTO.setIntro(book.getIntro());
        bookDTO.setCoverUrl(book.getCoverUrl());

        // Get chapters content (first 10)
        List<Chapter> chapters = chapterService.findChaptersByRange(bookId, 1, 10);
        List<ChapterDTO> chapterDTOs = chapters.stream().map(ch -> {
            ChapterDTO dto = new ChapterDTO();
            dto.setIndex(ch.getChapterIndex());
            dto.setTitle(ch.getChapterTitle());
            dto.setContent(chapterService.readChapterContent(ch));
            return dto;
        }).collect(Collectors.toList());

        // Get RAG status
        RagStatusDTO ragStatus = ragIndexService.getStatus(bookId);

        // Get quick prompt buttons
        List<PromptTemplateDTO> quickPrompts = promptTemplateService.findQuickButtons();

        // Build response
        AnalysisPageDTO result = new AnalysisPageDTO();
        result.setBook(bookDTO);
        result.setChapters(chapterDTOs);
        result.setRagStatus(ragStatus);
        result.setQuickPrompts(quickPrompts);

        return result;
    }
}
