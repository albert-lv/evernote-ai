package org.albert.evernote.ai.controller;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.EverNoteAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/evernote")
public class EverNoteController {

    private final EverNoteAiService everNoteAiService;

    @GetMapping("/ai/weekly/summary")
    public SseEmitter weeklySummary()
            throws TException, EDAMNotFoundException, EDAMSystemException, EDAMUserException {
        SseEmitter emitter = new SseEmitter();
        return everNoteAiService.generateWeeklySummaryStream(emitter);
    }
}
