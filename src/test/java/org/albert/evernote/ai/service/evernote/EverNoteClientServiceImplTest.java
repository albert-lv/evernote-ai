package org.albert.evernote.ai.service.evernote;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.thrift.TException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EverNoteClientServiceImplTest {

    @Autowired private EverNoteClientService everNoteClientService;

    @Test
    public void testSearchNotes()
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        List<String> notes = everNoteClientService.searchNotes("created:day-7");
        Assertions.assertTrue(notes.size() > 0);
    }

    @Test
    public void testSearchNoteMetadata()
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        List<NoteMetadata> notes =
                everNoteClientService.searchNoteMetadata("created:20151127 -created:20151128");
        Assertions.assertTrue(notes.size() > 0);
    }
}
