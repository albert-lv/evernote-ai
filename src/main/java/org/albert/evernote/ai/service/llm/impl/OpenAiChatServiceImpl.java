package org.albert.evernote.ai.service.llm.impl;

import io.github.resilience4j.retry.annotation.Retry;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.albert.evernote.ai.service.llm.LlmService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class OpenAiChatServiceImpl implements LlmService {

    private final OpenAiChatClient chatClient;

    @Retry(name = "openai")
    @Override
    public String generate(String promptString) {
        Prompt prompt = new Prompt(new UserMessage(promptString));
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Retry(name = "openai")
    @Override
    public SseEmitter generateStream(String promptString, SseEmitter emitter) {
        Prompt prompt = new Prompt(new UserMessage(promptString));
        Flux<ChatResponse> responseFlux = chatClient.stream(prompt);
        responseFlux.subscribe(
                data -> {
                    try {
                        AssistantMessage message = data.getResult().getOutput();
                        if (Objects.nonNull(message)) {
                            emitter.send(SseEmitter.event().id(null).data(message));
                        }
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError, // 处理错误
                emitter::complete // 完成时调用
                );
        return emitter;
    }
}
