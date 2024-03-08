package org.albert.evernote.ai.service.embedding;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransformerEmbeddingServiceImplTest {

    @Autowired EmbeddingService embeddingService;

    @Test
    public void testEmbed() {
        List<List<Double>> results = embeddingService.embed(List.of("hello", "world"));
        System.out.println(results);
        System.out.println(results.get(0).size());
    }
}
