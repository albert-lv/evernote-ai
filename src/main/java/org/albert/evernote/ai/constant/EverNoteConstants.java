package org.albert.evernote.ai.constant;

public class EverNoteConstants {

    public static final String EVERNOTE_NOTES_SINGLE_SUMMARY_PROMPT_TEMPLATE =
            "请总结下面的内容:\n\n" + "%s\n\n" + "以markdown格式返回，请使用中文。";
    public static final String EVERNOTE_NOTES_WEEKLY_SUMMARY_PROMPT_TEMPLATE =
            "最近一周收藏的文章或者网页总结内容（markdown格式）分别如下。\n\n"
                    + "%s\n\n"
                    + "请分门别类对所有文章或者网页内容进行总结归纳，以markdown的格式输出，请使用中文。";
}
