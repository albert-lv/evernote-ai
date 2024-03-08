package org.albert.evernote.ai.service.evernote;

import java.util.List;

public interface EverNotePromptService {

    String buildSingleSummaryPrompt(String promptTemplate, String note, int tokenLimit);

    String buildWeeklySummaryPrompt(String promptTemplate, List<String> notes, int tokenLimit);

    String buildRagPrompt(String promptTemplate, String query, List<String> docs, int tokenLimit);
}
