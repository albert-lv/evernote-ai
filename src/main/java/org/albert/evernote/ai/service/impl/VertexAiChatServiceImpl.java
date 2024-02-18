package org.albert.evernote.ai.service.impl;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.LlmService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertex.VertexAiChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class VertexAiChatServiceImpl implements LlmService {

    private final VertexAiChatClient chatClient;

    @Retry(name = "vertex-ai")
    @Override
    public String generate(String promptString) {
        Prompt prompt = new Prompt(new UserMessage(promptString));
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Retry(name = "vertex-ai")
    @Override
    public SseEmitter generateStream(String promptString, SseEmitter emitter) {
        throw new UnsupportedOperationException();
    }
}
