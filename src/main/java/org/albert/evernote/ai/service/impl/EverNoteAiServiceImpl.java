package org.albert.evernote.ai.service.impl;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.constant.EverNoteConstants;
import org.albert.evernote.ai.constant.LlmConstants;
import org.albert.evernote.ai.service.EverNoteAiService;
import org.albert.evernote.ai.service.EverNoteClientService;
import org.albert.evernote.ai.service.EverNotePromptService;
import org.albert.evernote.ai.service.LlmService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class EverNoteAiServiceImpl implements EverNoteAiService {

    private final LlmService ollamaChatServiceImpl;
    private final EverNotePromptService everNotePromptService;
    private final EverNoteClientService everNoteClientService;

    @Override
    public SseEmitter generateWeeklySummaryStream(SseEmitter emitter)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        List<String> notes = everNoteClientService.searchNotes("created:day-7");
        List<String> noteSummaries =
                notes.stream()
                        .map(
                                note -> {
                                    String message =
                                            everNotePromptService.buildEverNoteSingleSummaryPrompt(
                                                    EverNoteConstants
                                                            .EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE,
                                                    note,
                                                    LlmConstants.MAX_TOKENS);
                                    return ollamaChatServiceImpl.generate(message);
                                })
                        .collect(Collectors.toList());
        String message =
                everNotePromptService.buildEverNoteWeeklySummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE,
                        noteSummaries,
                        LlmConstants.MAX_TOKENS);
        return ollamaChatServiceImpl.generateStream(message, emitter);
    }
}
