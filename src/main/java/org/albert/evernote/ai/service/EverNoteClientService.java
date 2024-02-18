package org.albert.evernote.ai.service;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import java.util.List;

public interface EverNoteClientService {

    List<String> searchNotes(String query)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException;
}
