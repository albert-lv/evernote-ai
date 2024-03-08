package org.albert.evernote.ai.service.document.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.albert.evernote.ai.service.document.DocumentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {

    public static final int CHUNK_SIZE = 1024;

    private final Tika tika = new Tika();
    private final UniversalDetector detector = new UniversalDetector();
    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    @Override
    public String parseToPlainText(String doc) throws IOException, TikaException {
        byte[] rawBytes = doc.getBytes();
        String charsetName = detectCharset(rawBytes);
        try (InputStream stream = new ByteArrayInputStream(doc.getBytes(charsetName))) {
            String result = tika.parseToString(stream);
            result = result.replaceAll("\\s", "");
            return result;
        }
    }

    @Override
    public List<String> splitToBlocks(String text) {
        return tokenTextSplitter.split(text, CHUNK_SIZE);
    }

    private String detectCharset(byte[] bytes) {
        detector.handleData(bytes);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if (encoding == null) {
            encoding = StandardCharsets.UTF_8.toString();
        }
        detector.reset();
        return encoding;
    }
}
