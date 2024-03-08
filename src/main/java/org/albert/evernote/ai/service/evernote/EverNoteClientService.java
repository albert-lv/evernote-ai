package org.albert.evernote.ai.service.evernote;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import java.util.List;

public interface EverNoteClientService {

    List<String> searchNotes(String query)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException;

    List<NoteMetadata> searchNoteMetadata(String query)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException;

    Note getNote(String guid)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException;
}
