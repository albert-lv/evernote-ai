package org.albert.evernote.ai.service;

import java.util.List;

public interface EverNotePromptService {

    String buildEverNoteSingleSummaryPrompt(String promptTemplate, String note, int tokenLimit);

    String buildEverNoteWeeklySummaryPrompt(
            String promptTemplate, List<String> notes, int tokenLimit);
}
