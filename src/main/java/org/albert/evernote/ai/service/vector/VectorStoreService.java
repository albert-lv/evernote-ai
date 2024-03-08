package org.albert.evernote.ai.service.vector;

import java.util.List;
import org.albert.evernote.ai.service.vector.model.VectorDocument;

public interface VectorStoreService {

    void insert(List<VectorDocument> documents);

    List<VectorDocument> search(List<Float> vector, int limit);
}
