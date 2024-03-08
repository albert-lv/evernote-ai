package org.albert.evernote.ai.service.vector.impl;

import static org.albert.evernote.ai.service.vector.constant.Constants.FIELD_CONTENT;
import static org.albert.evernote.ai.service.vector.constant.Constants.FIELD_EMBEDDINGS;
import static org.albert.evernote.ai.service.vector.constant.Constants.FIELD_TITLE;
import static org.albert.evernote.ai.service.vector.constant.Constants.FIELD_URL;

import com.google.common.collect.Lists;
import io.milvus.param.dml.InsertParam.Field;
import io.milvus.response.SearchResultsWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.vector.VectorStoreService;
import org.albert.evernote.ai.service.vector.milvus.MilvusClient;
import org.albert.evernote.ai.service.vector.model.VectorDocument;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VectorStoreServiceImpl implements VectorStoreService {

    public static final int MAX_BATCH_SIZE = 100;
    public static final int MAX_META_LENGTH = 100;
    public static final int MAX_CONTENT_LENGTH = 65535;
    public static final String NONE = "NONE";

    private final MilvusClient milvusClient;

    @Override
    public void insert(List<VectorDocument> documents) {
        // 将文档列表分割成大小为100的子列表
        List<List<VectorDocument>> batches = Lists.partition(documents, MAX_BATCH_SIZE);
        for (List<VectorDocument> batch : batches) {
            // 为每个子列表创建字段
            List<Field> fields = createFieldsFromDocuments(batch);
            // 批量插入
            milvusClient.insert(fields);
        }
    }

    @NotNull
    private List<Field> createFieldsFromDocuments(List<VectorDocument> documents) {
        List<String> titles =
                documents.stream()
                        .map(VectorDocument::getTitle)
                        .map(content -> StringUtils.substring(content, 0, MAX_META_LENGTH))
                        .map(content -> StringUtils.isBlank(content) ? NONE : content)
                        .collect(Collectors.toList());
        List<String> contents =
                documents.stream()
                        .map(VectorDocument::getContent)
                        .map(content -> StringUtils.substring(content, 0, MAX_CONTENT_LENGTH))
                        .map(content -> StringUtils.isBlank(content) ? NONE : content)
                        .collect(Collectors.toList());
        List<String> urls =
                documents.stream()
                        .map(VectorDocument::getUrl)
                        .map(content -> StringUtils.substring(content, 0, MAX_META_LENGTH))
                        .map(content -> StringUtils.isBlank(content) ? NONE : content)
                        .collect(Collectors.toList());
        List<List<Float>> embeddings =
                documents.stream().map(VectorDocument::getEmbeddings).collect(Collectors.toList());
        List<Field> fields = new ArrayList<>();
        fields.add(new Field(FIELD_TITLE, titles));
        fields.add(new Field(FIELD_CONTENT, contents));
        fields.add(new Field(FIELD_URL, urls));
        fields.add(new Field(FIELD_EMBEDDINGS, embeddings));
        return fields;
    }

    @Override
    public List<VectorDocument> search(List<Float> vector, int limit) {
        List<String> searchOutputFields = Arrays.asList(FIELD_TITLE, FIELD_CONTENT, FIELD_URL);
        SearchResultsWrapper resultsWrapper =
                milvusClient.search(vector, limit, searchOutputFields);
        List<VectorDocument> results = new ArrayList<>();
        resultsWrapper
                .getIDScore(0)
                .forEach(
                        idScore -> {
                            Map<String, Object> fieldValues = idScore.getFieldValues();
                            String title = (String) fieldValues.get(FIELD_TITLE);
                            String content = (String) fieldValues.get(FIELD_CONTENT);
                            String url = (String) fieldValues.get(FIELD_URL);
                            results.add(
                                    VectorDocument.builder()
                                            .title(title)
                                            .content(content)
                                            .url(url)
                                            .build());
                        });
        return results;
    }
}
