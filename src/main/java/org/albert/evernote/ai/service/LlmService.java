package org.albert.evernote.ai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface LlmService {

    String generate(String promptString);

    SseEmitter generateStream(String promptString, SseEmitter emitter);
}
