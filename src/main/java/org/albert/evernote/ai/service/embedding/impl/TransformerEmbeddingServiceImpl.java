package org.albert.evernote.ai.service.embedding.impl;

import java.util.Collections;
import java.util.List;
import org.albert.evernote.ai.service.embedding.EmbeddingService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.stereotype.Service;

@Service
public class TransformerEmbeddingServiceImpl implements EmbeddingService {

    public static final String RESOURCE_CACHE_DIRECTORY = "/tmp/onnx-zoo";
    private TransformersEmbeddingClient embeddingClient;

    public TransformerEmbeddingServiceImpl() throws Exception {
        embeddingClient = new TransformersEmbeddingClient();
        embeddingClient.setResourceCacheDirectory(RESOURCE_CACHE_DIRECTORY);
        embeddingClient.afterPropertiesSet();
    }

    @Override
    public List<List<Double>> embed(List<String> texts) {
        if (CollectionUtils.isEmpty(texts)) {
            return Collections.EMPTY_LIST;
        }
        return embeddingClient.embed(texts);
    }
}
