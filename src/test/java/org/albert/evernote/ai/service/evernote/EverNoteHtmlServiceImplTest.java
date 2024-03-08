package org.albert.evernote.ai.service.evernote;

import static org.albert.evernote.ai.service.evernote.constant.Constants.SOURCE_URL;
import static org.albert.evernote.ai.service.evernote.constant.Constants.TITLE;

import java.util.Map;
import org.albert.evernote.ai.service.evernote.impl.EverNoteHtmlServiceImpl;
import org.junit.jupiter.api.Test;

public class EverNoteHtmlServiceImplTest {

    private EverNoteHtmlServiceImpl service = new EverNoteHtmlServiceImpl();

    @Test
    public void testParserMetaData() {
        String htmlPath = "/Users/albert/CodeProjects/evernote-ai/src/test/resources/evernote.html";
        Map<String, String> metaData = service.parseMetaData(htmlPath);
        System.out.println(metaData.get(TITLE));
        System.out.println(metaData.get(SOURCE_URL));
    }
}
