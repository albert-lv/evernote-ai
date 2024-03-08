package org.albert.evernote.ai.service.evernote;

import java.util.List;
import org.albert.evernote.ai.constant.EverNoteConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EverNotePromptServiceImplTest {

    @Autowired EverNotePromptService everNotePromptService;

    @Test
    public void testBuildEverNoteSingleSummaryPromptWith_Empty_Response() {
        String prompt =
                everNotePromptService.buildSingleSummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE,
                        "这是一段笔记的总结内容",
                        10);
        Assertions.assertTrue(StringUtils.isEmpty(prompt));
    }

    @Test
    public void testBuildEverNoteSingleSummaryPromptWith_Note_Extract() {
        String prompt =
                everNotePromptService.buildSingleSummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE,
                        "这是一段笔记的总结内容",
                        30);
        Assertions.assertEquals(
                "请总结下面的内容:\n" + "\n" + "这是一段笔记的总结内容\n" + "\n" + "以markdown格式返回，请使用中文。", prompt);
    }

    @Test
    public void testBuildEverNoteSingleSummaryPromptWithout_Note_Extract() {
        String prompt =
                everNotePromptService.buildSingleSummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE,
                        "这是一段笔记的总结内容",
                        40);
        Assertions.assertEquals(
                "请总结下面的内容:\n" + "\n" + "这是一段笔记的总结内容\n" + "\n" + "以markdown格式返回，请使用中文。", prompt);
    }

    @Test
    public void testBuildEverNoteWeeklySummaryPromptWith_Empty_Response() {
        String prompt =
                everNotePromptService.buildWeeklySummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE,
                        mockNotes(),
                        10);
        Assertions.assertTrue(StringUtils.isEmpty(prompt));
    }

    @Test
    public void testBuildEverNoteWeeklySummaryPromptWith_Note_Ignore() {
        String prompt =
                everNotePromptService.buildWeeklySummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE,
                        mockNotes(),
                        70);
        Assertions.assertEquals(
                "最近一周收藏的文章或者网页总结内容（markdown格式）分别如下。\n"
                        + "\n"
                        + "这是一段笔记的总结内容\n"
                        + "\n"
                        + "\n"
                        + "请分门别类对所有文章或者网页内容进行总结归纳，以markdown的格式输出，请使用中文。",
                prompt);
    }

    @Test
    public void testBuildEverNoteWeeklySummaryPromptWithout_Note_Ignore() {
        String prompt =
                everNotePromptService.buildWeeklySummaryPrompt(
                        EverNoteConstants.EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE,
                        mockNotes(),
                        80);
        Assertions.assertEquals(
                "最近一周收藏的文章或者网页总结内容（markdown格式）分别如下。\n"
                        + "\n"
                        + "这是一段笔记的总结内容\n"
                        + "这是一段笔记的总结内容\n"
                        + "\n"
                        + "\n"
                        + "请分门别类对所有文章或者网页内容进行总结归纳，以markdown的格式输出，请使用中文。",
                prompt);
    }

    private List<String> mockNotes() {
        List<String> notes = List.of("这是一段笔记的总结内容", "这是一段笔记的总结内容", "这是一段笔记的总结内容");
        return notes;
    }
}
