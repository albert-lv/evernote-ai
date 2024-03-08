package org.albert.evernote.ai.service.vector.milvus;

import static org.albert.evernote.ai.service.vector.constant.Constants.COLLECTION_NAME;
import static org.albert.evernote.ai.service.vector.constant.Constants.FIELD_EMBEDDINGS;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.InsertParam.Field;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MilvusClient {

    public static final String SEARCH_PARAM = "{\"nprobe\":10, \"offset\":0}";
    private final MilvusServiceClient milvusServiceClient;

    public MilvusClient(
            @Value("${milvus.host}") String host,
            @Value("${milvus.port}") int port,
            @Value("${milvus.username}") String username,
            @Value("${milvus.password}") String password) {
        ConnectParam connectParam =
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .withAuthorization(username, password)
                        .build();
        milvusServiceClient = new MilvusServiceClient(connectParam);
    }

    public void insert(List<Field> fields) {
        InsertParam insertParam =
                InsertParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .withFields(fields)
                        .build();
        milvusServiceClient.insert(insertParam);
    }

    public SearchResultsWrapper search(
            List<Float> vector, Integer topK, List<String> outputFields) {
        milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder().withCollectionName(COLLECTION_NAME).build());

        SearchParam searchParam =
                SearchParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                        .withMetricType(MetricType.COSINE)
                        .withOutFields(outputFields)
                        .withTopK(topK)
                        .withVectors(Arrays.asList(vector))
                        .withVectorFieldName(FIELD_EMBEDDINGS)
                        .withParams(MilvusClient.SEARCH_PARAM)
                        .build();
        R<SearchResults> respSearch = milvusServiceClient.search(searchParam);
        return new SearchResultsWrapper(respSearch.getData().getResults());
    }
}
