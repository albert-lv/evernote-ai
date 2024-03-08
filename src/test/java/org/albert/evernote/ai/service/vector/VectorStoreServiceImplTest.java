package org.albert.evernote.ai.service.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.albert.evernote.ai.service.embedding.EmbeddingService;
import org.albert.evernote.ai.service.vector.model.VectorDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VectorStoreServiceImplTest {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String URL = "url";

    @Autowired EmbeddingService embeddingService;
    @Autowired VectorStoreService vectorStoreService;

    @Test
    public void testInsert() {
        List<VectorDocument> documents = new ArrayList<>();
        documents.add(mockVectorDocument());
        vectorStoreService.insert(documents);
    }

    @Test
    public void testSearch() {
        List<Float> embedding =
                embeddingService.embed(Arrays.asList("如何准备好Java技术面试?")).get(0).stream()
                        .map(Double::floatValue)
                        .collect(Collectors.toList());
        List<VectorDocument> results = vectorStoreService.search(embedding, 10);
        Assertions.assertTrue(results.size() > 0);
    }

    private VectorDocument mockVectorDocument() {
        List<Float> embeddings =
                embeddingService.embed(Arrays.asList(CONTENT)).get(0).stream()
                        .map(Double::floatValue)
                        .collect(Collectors.toList());
        return VectorDocument.builder()
                .title(TITLE)
                .content(CONTENT)
                .url(URL)
                .embeddings(embeddings)
                .build();
    }
}
