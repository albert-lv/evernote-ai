package org.albert.evernote.ai.service.evernote;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EverNoteAiService {

    SseEmitter weeklySummaryStream(SseEmitter emitter)
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException;

    SseEmitter ragStream(String query, SseEmitter emitter);
}
