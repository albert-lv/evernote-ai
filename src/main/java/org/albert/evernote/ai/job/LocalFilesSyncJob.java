package org.albert.evernote.ai.job;

import static org.albert.evernote.ai.service.evernote.constant.Constants.SOURCE_URL;
import static org.albert.evernote.ai.service.evernote.constant.Constants.TITLE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.document.DocumentService;
import org.albert.evernote.ai.service.embedding.EmbeddingService;
import org.albert.evernote.ai.service.evernote.EverNoteHtmlService;
import org.albert.evernote.ai.service.vector.VectorStoreService;
import org.albert.evernote.ai.service.vector.model.VectorDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LocalFilesSyncJob {

    private static final String PROCESSED_FILES_LOG = "processed-files.log";

    private final EverNoteHtmlService everNoteHtmlService;
    private final DocumentService documentService;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public void runJob() {
        try {
            Path directoryPath = Paths.get("/Users/albert/YinXiang");
            processHtmlFilesInDirectory(directoryPath);
        } catch (Exception e) {
            System.out.println("处理目录时发生异常：" + e.getMessage());
        }
    }

    // 处理HTML文件的函数，将文件内容读取为String
    private boolean processHtmlFile(Path filePath) {
        try {
            Map<String, String> metaData = everNoteHtmlService.parseMetaData(filePath.toString());
            String content = new String(Files.readAllBytes(filePath));
            String title = metaData.get(TITLE);
            String url = metaData.get(SOURCE_URL);

            String text = documentService.parseToPlainText(content);
            List<String> blocks = documentService.splitToBlocks(text);
            List<List<Double>> embeddings = embeddingService.embed(blocks);

            // Skip empty content
            if (CollectionUtils.isEmpty(blocks) || CollectionUtils.isEmpty(embeddings)) {
                return false;
            }

            List<VectorDocument> vectorDocuments =
                    IntStream.range(0, blocks.size())
                            .mapToObj(
                                    index ->
                                            VectorDocument.builder()
                                                    .title(title)
                                                    .content(
                                                            blocks.get(
                                                                    index)) // Use the block instead
                                                    // of content
                                                    .url(url)
                                                    .embeddings(
                                                            embeddings.get(index).stream()
                                                                    .map(Double::floatValue)
                                                                    .collect(Collectors.toList()))
                                                    .build())
                            .collect(Collectors.toList());

            vectorStoreService.insert(vectorDocuments);
            return true;
        } catch (Exception e) {
            System.out.println("处理文件时发生异常：" + filePath + ", 异常信息：" + e.getMessage());
            // 如果处理失败，返回false
            return false;
        }
    }

    // 检查文件是否已经处理过
    private static boolean isFileProcessed(Path filePath, Set<Path> processedFiles) {
        return processedFiles.contains(filePath);
    }

    // 记录已处理的文件
    private static void logProcessedFile(Path filePath, Set<Path> processedFiles)
            throws IOException {
        processedFiles.add(filePath);
        try (BufferedWriter writer =
                Files.newBufferedWriter(
                        Paths.get(PROCESSED_FILES_LOG),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND)) {
            writer.write(filePath.toString());
            writer.newLine();
        }
    }

    // 从文件加载已处理的文件列表
    private static Set<Path> loadProcessedFiles() throws IOException {
        Set<Path> processedFiles = new HashSet<>();
        if (Files.exists(Paths.get(PROCESSED_FILES_LOG))) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(PROCESSED_FILES_LOG))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processedFiles.add(Paths.get(line));
                }
            }
        }
        return processedFiles;
    }

    // 遍历文件夹，处理HTML文件
    private void processHtmlFilesInDirectory(Path directoryPath) throws IOException {
        Set<Path> processedFiles = loadProcessedFiles(); // 加载已处理的文件列表
        File directory = directoryPath.toFile();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // 只处理文件，忽略子文件夹
                if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".html")) {
                    Path filePath = file.toPath();
                    // 检查文件是否已经处理过
                    if (!isFileProcessed(filePath, processedFiles)) {
                        boolean success = processHtmlFile(filePath);
                        if (success) {
                            logProcessedFile(filePath, processedFiles); // 记录成功的处理
                            System.out.println("处理文件成功：" + filePath);
                        } else {
                            System.out.println("处理文件失败：" + filePath);
                        }
                    }
                }
            }
        }
    }
}
