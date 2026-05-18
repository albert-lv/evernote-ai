# Evernote AI

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

中文文档 | **[English](./README.md)**

一个智能的印象笔记 AI 助手,提供每周摘要和检索增强生成 (RAG) 功能。本项目展示了如何构建一个生产就绪的 AI 系统,集成印象笔记、执行语义搜索,并使用向量数据库和大语言模型生成上下文感知的响应。

## 目录

- [功能特性](#功能特性)
- [架构设计](#架构设计)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 接口](#api-接口)
- [可复用组件](#可复用组件)
- [开发指南](#开发指南)
- [许可证](#许可证)

## 功能特性

### 1. 每周摘要生成
- 自动生成基于 AI 的每周印象笔记活动摘要
- 使用服务器发送事件 (SSE) 实时流式响应
- 分析笔记内容并提供深入的摘要

### 2. 检索增强生成 (RAG)
- 使用向量嵌入对印象笔记内容进行语义搜索
- 基于实际笔记内容生成上下文感知的响应
- 实时流式响应,提供更好的用户体验

### 3. 自动同步
- 后台任务自动将印象笔记同步到向量数据库
- 支持本地文件同步
- 增量同步并记录日期

## 架构设计

本项目采用清晰的分层架构:

```
┌─────────────────────────────────────────────────┐
│           REST API 层                            │
│  (EverNoteController - SSE 流式传输)            │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│        服务层 (核心业务逻辑)                      │
│  • EverNoteAiService: AI 编排                   │
│  • LlmService: LLM 交互                         │
│  • VectorStoreService: 向量操作                 │
│  • EmbeddingService: 文本嵌入                   │
│  • DocumentService: 文本处理                    │
│  • EverNoteClientService: 印象笔记 API          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         后台任务与工具                            │
│  • EverNoteSyncJob: 同步笔记到向量数据库         │
│  • LocalFilesSyncJob: 同步本地文件              │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         外部集成                                  │
│  • Evernote API                                 │
│  • OpenAI / Ollama / Vertex AI                  │
│  • Milvus 向量数据库                             │
└─────────────────────────────────────────────────┘
```

## 技术栈

### 核心框架
- **Spring Boot 3.2.2** - 应用框架
- **Java 21** - 编程语言
- **Maven** - 构建工具

### AI 与机器学习
- **Spring AI 0.8.0** - Java AI 框架
  - OpenAI 集成
  - Ollama 集成 (本地 LLM 支持)
  - Vertex AI 集成
  - Transformer 嵌入模型
- **JTokkit 1.0.0** - OpenAI 模型 Token 计数

### 向量数据库
- **Milvus 2.3.4** - 高性能向量数据库,用于语义搜索

### 文档处理
- **Apache Tika 2.9.0** - 文本提取和文档解析
- **Juniversalchardet 1.0.3** - 字符编码检测

### 印象笔记集成
- **Evernote API 1.25.1** - 印象笔记官方 SDK

### 工具库
- **Guava 33.0.0** - Google 核心库
- **Resilience4j** - 熔断器和重试逻辑
- **Lombok** - 减少样板代码
- **Spotless** - 代码格式化

## 快速开始

### 前置要求

1. **Java 21** 或更高版本
2. **Maven 3.6+**
3. **Milvus** 向量数据库 (可通过 Docker 运行)
4. **印象笔记开发者令牌** - [获取地址](https://dev.evernote.com/doc/)
5. **OpenAI API Key** (或其他 LLM 提供商凭证)

### 安装步骤

1. 克隆仓库:
```bash
git clone https://github.com/albert-lv/evernote-ai.git
cd evernote-ai
```

2. 创建 `application.properties` 文件:
```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

3. 配置应用 (参见 [配置说明](#配置说明))

4. 构建项目:
```bash
./mvnw clean package
```

5. 运行应用:
```bash
./mvnw spring-boot:run
```

## 配置说明

创建或更新 `src/main/resources/application.properties`:

```properties
# OpenAI 配置
spring.ai.openai.chat.base-url=https://api.openai.com
spring.ai.openai.chat.api-key=your-api-key
spring.ai.openai.chat.options.model=gpt-3.5-turbo-16k
spring.ai.openai.chat.options.temperature=0.7

# OpenAI 嵌入模型配置
spring.ai.openai.embedding.base-url=https://api.openai.com
spring.ai.openai.embedding.api-key=your-api-key

# OpenAI 图像配置 (可选)
spring.ai.openai.image.base-url=https://api.openai.com
spring.ai.openai.image.api-key=your-api-key

# Ollama 配置 (本地 LLM)
spring.ai.ollama.chat.options.model=llama2-chinese
spring.ai.ollama.chat.options.temperature=0.7

# Vertex AI 配置 (可选)
spring.ai.vertex.ai.base-url=your-vertex-ai-url
spring.ai.vertex.ai.api-key=your-api-key
spring.ai.vertex.ai.chat.model=gemini-pro
spring.ai.vertex.ai.chat.options.temperature=0.7

# Resilience4j 重试配置
resilience4j.retry.configs.default.maxRetryAttempts=3
resilience4j.retry.configs.default.waitDuration=1000

# 印象笔记开发者令牌
evernote.developer.token=your-evernote-dev-token
```

## API 接口

### 1. 每周摘要
```
GET /api/v1/evernote/ai/weekly/summary
```
生成基于 AI 的每周印象笔记活动摘要。返回 SSE 流式响应。

**响应:** 服务器发送事件 (SSE) 流

**示例:**
```bash
curl -N http://localhost:8080/api/v1/evernote/ai/weekly/summary
```

### 2. RAG 查询
```
GET /api/v1/evernote/ai/rag?query=<你的问题>
```
在笔记中执行语义搜索并生成上下文感知的响应。

**参数:**
- `query` (必需): 你的问题或搜索查询

**响应:** 服务器发送事件 (SSE) 流

**示例:**
```bash
curl -N "http://localhost:8080/api/v1/evernote/ai/rag?query=我学到了什么关于%20Spring%20Boot%3F"
```

## 可复用组件

本项目包含多个可复用组件,可以适配到其他应用中:

### 1. 文档处理服务
**位置:** `org.albert.evernote.ai.service.document.DocumentService`

**用途:** 解析文档并拆分成可处理的块

**主要特性:**
- 使用 Apache Tika 将 HTML/XML 转换为纯文本
- 为嵌入模型智能分块文本
- 字符编码检测

**可复用性:** 可用于任何需要为 LLM 或嵌入模型准备文本的文档处理管道。

### 2. 嵌入服务
**位置:** `org.albert.evernote.ai.service.embedding.EmbeddingService`

**用途:** 从文本生成向量嵌入

**主要特性:**
- 支持多种嵌入模型 (OpenAI, Transformers)
- 批处理能力
- 模型无关接口

**可复用性:** 可作为任何需要文本嵌入的应用的即插即用解决方案。

### 3. 向量存储服务
**位置:** `org.albert.evernote.ai.service.vector.VectorStoreService`

**用途:** 管理向量数据库操作

**主要特性:**
- 插入和搜索向量文档
- Milvus 集成与连接池
- 通用向量文档模型

**可复用性:** 可适配任何向量数据库使用场景(语义搜索、推荐系统等)。

### 4. LLM 服务
**位置:** `org.albert.evernote.ai.service.llm.LlmService`

**用途:** 与大语言模型交互

**主要特性:**
- 支持多个 LLM 提供商 (OpenAI, Ollama, Vertex AI)
- 流式响应支持
- 使用 Resilience4j 的重试逻辑

**可复用性:** 可用于任何 AI 应用的通用 LLM 交互接口。

### 5. 同步任务框架
**位置:** `org.albert.evernote.ai.job.*`

**用途:** 内容到向量数据库的后台同步

**主要特性:**
- 带检查点持久化的增量同步
- 错误处理和速率限制
- 针对不同数据源的模块化设计 (印象笔记、本地文件)

**可复用性:** 可作为构建任何数据源后台同步任务的模板。

### 6. Milvus 客户端封装
**位置:** `org.albert.evernote.ai.service.vector.milvus.MilvusClient`

**用途:** 简化 Milvus 向量数据库操作

**主要特性:**
- 常用操作的高级 API
- 连接管理
- Schema 和 Collection 管理

**可复用性:** 可作为独立的 Milvus 工具库提取。

## 开发指南

### 代码格式化
本项目使用 Spotless 进行代码格式化:

```bash
# 检查格式
./mvnw spotless:check

# 应用格式化
./mvnw spotless:apply
```

### 运行测试
```bash
./mvnw test
```

### 生产构建
```bash
./mvnw clean package -DskipTests
java -jar target/evernote-ai-0.0.1-SNAPSHOT.jar
```

## 项目结构

```
src/main/java/org/albert/evernote/ai/
├── controller/          # REST API 端点
├── service/
│   ├── document/       # 文档处理
│   ├── embedding/      # 文本嵌入
│   ├── evernote/       # 印象笔记集成
│   ├── llm/            # LLM 交互
│   └── vector/         # 向量数据库操作
├── job/                # 后台同步任务
├── util/               # 工具类
└── constant/           # 常量

src/test/java/           # 单元测试和集成测试
```

## 贡献

欢迎贡献! 请随时提交 Pull Request。

## 许可证

本项目使用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

## 致谢

- [Spring AI](https://spring.io/projects/spring-ai) - Java AI 框架
- [Milvus](https://milvus.io/) - 向量数据库
- [Evernote API](https://dev.evernote.com/) - 印象笔记 SDK
- [Apache Tika](https://tika.apache.org/) - 内容分析工具包
