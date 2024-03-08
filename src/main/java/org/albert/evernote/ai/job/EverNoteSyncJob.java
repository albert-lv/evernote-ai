package org.albert.evernote.ai.job;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.document.DocumentService;
import org.albert.evernote.ai.service.embedding.EmbeddingService;
import org.albert.evernote.ai.service.evernote.EverNoteClientService;
import org.albert.evernote.ai.service.vector.VectorStoreService;
import org.albert.evernote.ai.service.vector.model.VectorDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EverNoteSyncJob {

    private static final String DATE_FILE = "search-date.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final DocumentService documentService;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final EverNoteClientService everNoteClientService;

    public void runJob()
            throws TException,
                    EDAMNotFoundException,
                    EDAMSystemException,
                    EDAMUserException,
                    TikaException,
                    IOException,
                    InterruptedException {
        while (true) {
            LocalDate searchDate = getRecordDateFromFile();

            syncEverNoteAtDate(searchDate);

            searchDate = searchDate.plusDays(1); // 增加一天
            updateDateToFile(searchDate);
        }
    }

    private void syncEverNoteAtDate(LocalDate date)
            throws TException,
                    EDAMNotFoundException,
                    EDAMSystemException,
                    EDAMUserException,
                    TikaException,
                    IOException,
                    InterruptedException {
        List<NoteMetadata> metadataList =
                everNoteClientService.searchNoteMetadata(composeQuery(date));
        System.out.println(date.format(DATE_FORMATTER) + ": " + metadataList.size());
        int count = 0;
        for (NoteMetadata metadata : metadataList) {
            String title = metadata.getTitle();
            String url = metadata.getAttributes().getSourceURL();
            Note note = everNoteClientService.getNote(metadata.getGuid());
            String content = note.getContent();
            String text = documentService.parseToPlainText(content);
            List<String> blocks = documentService.splitToBlocks(text);
            List<List<Double>> embeddings = embeddingService.embed(blocks);

            // Skip empty content
            if (CollectionUtils.isEmpty(blocks) || CollectionUtils.isEmpty(embeddings)) {
                continue;
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

            count++;
            System.out.println(count + "/" + metadataList.size());
            Thread.sleep(Duration.ofSeconds(3));
        }
    }

    private String composeQuery(LocalDate date) {
        return "created:"
                + date.format(DATE_FORMATTER)
                + " -created:"
                + (date.plusDays(1).format(DATE_FORMATTER));
    }

    private LocalDate getRecordDateFromFile() {
        File dateFile = new File(DATE_FILE);
        LocalDate currentDate = LocalDate.of(2015, 1, 1);
        if (dateFile.exists() && !dateFile.isDirectory()) {
            try (BufferedReader br = new BufferedReader(new FileReader(dateFile))) {
                String dateStr = br.readLine();
                currentDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                dateFile.createNewFile(); // 创建文件
                updateDateToFile(currentDate); // 写入当前日期
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return currentDate;
    }

    private void updateDateToFile(LocalDate date) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATE_FILE))) {
            bw.write(date.format(DATE_FORMATTER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
