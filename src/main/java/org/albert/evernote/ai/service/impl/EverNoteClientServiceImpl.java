package org.albert.evernote.ai.service.impl;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.albert.evernote.ai.service.EverNoteClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class EverNoteClientServiceImpl implements EverNoteClientService {

    private final NoteStoreClient noteStore;

    public EverNoteClientServiceImpl(@Value("${evernote.developer.token}") String developerToken)
        throws TException, EDAMSystemException, EDAMUserException {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.YINXIANG, developerToken);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        noteStore = factory.createNoteStoreClient();
    }

    @Retry(name = "evernote")
    @Override
    public List<String> searchNotes(String query)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        NoteFilter filter = new NoteFilter();
        filter.setAscending(Boolean.FALSE);
        filter.setWords(query);

        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(Boolean.TRUE);

        NotesMetadataList ourNoteList = noteStore.findNotesMetadata(filter, 0, 100, spec);

        List<Note> notes = new ArrayList<>();
        for (NoteMetadata noteMetadata : ourNoteList.getNotes()) {
            Note note =
                    noteStore.getNote(
                            noteMetadata.getGuid(),
                            Boolean.TRUE,
                            Boolean.FALSE,
                            Boolean.FALSE,
                            Boolean.FALSE);
            notes.add(note);
        }

        return notes.stream()
                .map(note -> note.getTitle() + "\n\n" + note.getContent())
                .collect(Collectors.toList());
    }
}
