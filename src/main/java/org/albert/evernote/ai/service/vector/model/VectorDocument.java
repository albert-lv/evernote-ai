package org.albert.evernote.ai.service.vector.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VectorDocument {
    private String title;
    private String content;
    private String url;
    private List<Float> embeddings;
}
