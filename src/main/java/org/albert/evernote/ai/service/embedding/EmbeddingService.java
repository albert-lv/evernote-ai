package org.albert.evernote.ai.service.embedding;

import java.util.List;

public interface EmbeddingService {

    List<List<Double>> embed(List<String> docs);
}
