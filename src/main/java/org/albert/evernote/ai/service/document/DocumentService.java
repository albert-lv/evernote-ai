package org.albert.evernote.ai.service.document;

import java.io.IOException;
import java.util.List;
import org.apache.tika.exception.TikaException;

public interface DocumentService {

    String parseToPlainText(String doc) throws IOException, TikaException;

    List<String> splitToBlocks(String text);
}
