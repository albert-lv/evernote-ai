### 配置模板

在启动之前需要配置好"application.properties"文件

```properties
spring.ai.openai.chat.base-url=xxx
spring.ai.openai.chat.api-key=xxx
spring.ai.openai.chat.options.model=gpt-3.5-turbo-16k
spring.ai.openai.chat.options.temperature=0.7

spring.ai.openai.embedding.base-url=xxx
spring.ai.openai.embedding.api-key=xxx

spring.ai.openai.image.base-url=xxx
spring.ai.openai.image.api-key=xxx

spring.ai.ollama.chat.options.model=llama2-chinese
spring.ai.ollama.chat.options.temperature=0.7

spring.ai.vertex.ai.ai.base-url=xxx
spring.ai.vertex.ai.api-key=xxx
spring.ai.vertex.ai.chat.model=gemini-pro
spring.ai.vertex.ai.chat.options.temperature=0.7

resilience4j.retry.configs.default.maxRetryAttempts=3
resilience4j.retry.configs.default.waitDuration=1000

evernote.developer.token=xxx
```