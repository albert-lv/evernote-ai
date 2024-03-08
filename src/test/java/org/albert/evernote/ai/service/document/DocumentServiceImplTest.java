package org.albert.evernote.ai.service.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentServiceImplTest {

    @Autowired DocumentService documentService;

    @Test
    public void testParseSimpleHtmlToPlainText() throws IOException, TikaException {
        InputStream stream =
                DocumentServiceImplTest.class.getClassLoader().getResourceAsStream("index.html");
        String htmlDoc = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        String text = documentService.parseToPlainText(htmlDoc);
        System.out.println(text);
    }

    @Test
    public void testParseEverNoteHtmlToPlainText() throws IOException, TikaException {
        InputStream stream =
                DocumentServiceImplTest.class.getClassLoader().getResourceAsStream("evernote.html");
        String htmlDoc = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        String text = documentService.parseToPlainText(htmlDoc);
        System.out.println(text);
    }

    @Test
    public void testSplitToBlocks() throws IOException, TikaException {
        InputStream stream =
                DocumentServiceImplTest.class.getClassLoader().getResourceAsStream("doc.txt");
        String doc = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        List<String> blocks = documentService.splitToBlocks(doc);
        blocks.forEach(
                block -> {
                    System.out.println("==================================");
                    System.out.println(block);
                });
    }
}
