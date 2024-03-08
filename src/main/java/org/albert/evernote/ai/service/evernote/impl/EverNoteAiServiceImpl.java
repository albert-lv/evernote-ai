package org.albert.evernote.ai.service.evernote.impl;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.constant.EverNoteConstants;
import org.albert.evernote.ai.constant.LlmConstants;
import org.albert.evernote.ai.service.embedding.EmbeddingService;
import org.albert.evernote.ai.service.evernote.EverNoteAiService;
import org.albert.evernote.ai.service.evernote.EverNoteClientService;
import org.albert.evernote.ai.service.evernote.EverNotePromptService;
import org.albert.evernote.ai.service.llm.LlmService;
import org.albert.evernote.ai.service.vector.VectorStoreService;
import org.albert.evernote.ai.service.vector.model.VectorDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class EverNoteAiServiceImpl implements EverNoteAiService {

    private static final int LIMIT = 20;

    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final EverNotePromptService everNotePromptService;
    private final EverNoteClientService everNoteClientService;
    private final LlmService openAiChatServiceImpl;

    @Override
    public SseEmitter weeklySummaryStream(SseEmitter emitter)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        List<String> notes = everNoteClientService.searchNotes("created:day-7");
        List<String> noteSummaries =
                notes.stream()
                        .map(
                                note -> {
                                    String message =
                                            everNotePromptService.buildSingleSummaryPrompt(
                                                    EverNoteConstants
                                                            .EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE,
                                                    note,
                                                    LlmConstants.MAX_TOKENS);
                                    return openAiChatServiceImpl.generate(message);
                                })
                        .collect(Collectors.toList());
        String prompt =
                everNotePromptService.buildWeeklySummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE,
                        noteSummaries,
                        LlmConstants.MAX_TOKENS);
        return openAiChatServiceImpl.generateStream(prompt, emitter);
    }

    @Override
    public SseEmitter ragStream(String query, SseEmitter emitter) {
        List<List<Double>> results = embeddingService.embed(Arrays.asList(query));
        List<Float> embedding =
                results.get(0).stream().map(Double::floatValue).collect(Collectors.toList());
        List<VectorDocument> docs = vectorStoreService.search(embedding, LIMIT);
        String prompt =
                everNotePromptService.buildRagPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_RAG_PROMPT_TEMPLATE,
                        query,
                        docs.stream().map(VectorDocument::getContent).collect(Collectors.toList()),
                        LlmConstants.MAX_TOKENS);
        return openAiChatServiceImpl.generateStream(prompt, emitter);
    }
}
