package org.albert.evernote.ai.service.impl;

import java.util.Formatter;
import java.util.List;
import org.albert.evernote.ai.service.EverNotePromptService;
import org.albert.evernote.ai.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EverNotePromptServiceImpl implements EverNotePromptService {

    @Override
    public String buildEverNoteSingleSummaryPrompt(
            String promptTemplate, String note, int tokenLimit) {
        Formatter formatter = new Formatter();
        int currentTokenLength = TokenUtil.countTokens(promptTemplate);
        int leftTokenLength = tokenLimit - currentTokenLength;
        if (leftTokenLength <= 0) {
            return StringUtils.EMPTY;
        }
        int noteTokenLength = TokenUtil.countTokens(note);
        while (leftTokenLength < noteTokenLength) {
            int subStringLength =
                    (int) Math.floor(note.length() * (leftTokenLength / (double) noteTokenLength));
            note = note.substring(0, subStringLength);
            noteTokenLength = TokenUtil.countTokens(note);
        }
        return formatter.format(promptTemplate, note).toString();
    }

    @Override
    public String buildEverNoteWeeklySummaryPrompt(
            String promptTemplate, List<String> notes, int tokenLimit) {
        Formatter formatter = new Formatter();
        int totalTokenLength = TokenUtil.countTokens(promptTemplate);
        if (totalTokenLength >= tokenLimit) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        for (String note : notes) {
            int noteTokenLength = TokenUtil.countTokens(note);
            if (totalTokenLength + noteTokenLength > tokenLimit) {
                break;
            }
            totalTokenLength += noteTokenLength;
            sb.append(note).append("\n");
        }
        return formatter.format(promptTemplate, sb).toString();
    }
}
