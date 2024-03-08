package org.albert.evernote.ai.service.evernote.impl;

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
import org.albert.evernote.ai.service.evernote.EverNoteClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EverNoteClientServiceImpl implements EverNoteClientService {

    public static final int MAX_COUNT = 499;
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
        filter.setWords(query);

        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(Boolean.TRUE);
        spec.setIncludeAttributes(Boolean.TRUE);

        NotesMetadataList metadataList;
        List<NoteMetadata> allNotes = new ArrayList<>();
        do {
            metadataList = noteStore.findNotesMetadata(filter, allNotes.size(), MAX_COUNT, spec);
            allNotes.addAll(metadataList.getNotes());
        } while (allNotes.size() < metadataList.getTotalNotes()
                && metadataList.getNotes().size() == MAX_COUNT);

        List<Note> notes = new ArrayList<>();
        for (NoteMetadata noteMetadata : metadataList.getNotes()) {
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

    @Override
    public List<NoteMetadata> searchNoteMetadata(String query)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        NoteFilter filter = new NoteFilter();
        filter.setWords(query);

        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(Boolean.TRUE);
        spec.setIncludeAttributes(Boolean.TRUE);

        NotesMetadataList metadataList;
        List<NoteMetadata> allNotes = new ArrayList<>();
        do {
            metadataList = noteStore.findNotesMetadata(filter, allNotes.size(), MAX_COUNT, spec);
            allNotes.addAll(metadataList.getNotes());
        } while (allNotes.size() < metadataList.getTotalNotes()
                && metadataList.getNotes().size() == MAX_COUNT);
        return metadataList.getNotes();
    }

    @Override
    public Note getNote(String guid)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        return noteStore.getNote(guid, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
    }
}
