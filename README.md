# SakuraChat

<div align="center">
  <img src="./web-dashboard/public/logo.svg" alt="SakuraChat Logo" width="120" height="120" />
  <h1>SakuraChat</h1>
  <p>遇见未来，与 AI 温柔对话</p>
  <div>
    <a href="https://github.com/LovelyCatEx/SakuraChat/stargazers"><img src="https://img.shields.io/github/stars/LovelyCatEx/SakuraChat" alt="GitHub stars" /></a>
    <a href="https://github.com/LovelyCatEx/SakuraChat/forks"><img src="https://img.shields.io/github/forks/LovelyCatEx/SakuraChat" alt="GitHub forks" /></a>
    <a href="https://github.com/LovelyCatEx/SakuraChat/issues"><img src="https://img.shields.io/github/issues/LovelyCatEx/SakuraChat" alt="GitHub issues" /></a>
    <a href="https://github.com/LovelyCatEx/SakuraChat/blob/main/LICENSE"><img src="https://img.shields.io/github/license/LovelyCatEx/SakuraChat" alt="GitHub license" /></a>
  </div>
</div>

## 0x00 项目简介

SakuraChat 是一个现代化的 AI 聊天系统，致力于将强大的 AI 技术与极简的设计美学融合。无论是日常对话还是专业咨询，都能根据你的定制，用自然且精准的交互衔接每一次交互。

## 0x01 主要特性

### 核心功能
- **多用户管理**：支持多个用户同时使用系统
- **模型管理**：管理第三方 AI 模型
- **智能体管理**：管理和配置 AI 智能体
- **积分管理**：用户积分系统管理
- **第三方账号管理**：支持绑定和管理第三方账号
- **OpenAI 格式**：支持 OpenAI API 格式的请求和响应
- **跨平台上下文**：在不同平台间保持对话上下文

### 平台支持
| 功能 | QQ | 飞书 | Web |
|------|------|--------|----------|
| 文本 | ✅ | ✅ | 📅 |
| @/回复 | ✅ | 📅 | 📅 |
| 内置表情 | ✅ | 📅 | 📅 |
| 语音识别 | 📅 | ❌ | 📅 |
| 语音合成 | 📅 | ❌ | 📅 |
| 图片分析 | 📅 | 📅 | 📅 |
| 图片生成 | 📅 | 📅 | 📅 |

### 开发计划
- **语音识别**：将语音转换为文本
- **语音合成**：将文本转换为语音
- **图像识别**：分析和识别图片内容
- **图像生成**：根据描述生成图片
- **发送表情包**：支持发送和管理表情包
- **Gemini 格式**：支持 Google Gemini API 格式
- **Anthropic 格式**：支持 Anthropic API 格式

## 0x02 技术栈

### 后端
- **语言**：Kotlin/Java
- **框架**：Spring Boot 3
- **数据库**：Hibernate/JPA
- **认证**：SpringSecurity、JWT
- **第三方集成**：Lark (飞书), NapCat (QQ)

### 前端
- **语言**：TypeScript/JavaScript
- **框架**：React
- **构建工具**：Vite
- **UI 组件**：Ant Design
- **路由**：React Router

## 0x03 部署指南

### Docker

docker-compose.yml

```yml
name: SakuraChat

services:
  mysql:
    container_name: sakurachat-mysql
    image: lovelycatv/sakurachat-mysql:latest
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD}
    healthcheck:
      test: "/usr/bin/mysql --user=root --password=123456 --execute \"SHOW DATABASES;\""
      interval: 3s
      timeout: 1s
      retries: 5
    restart: unless-stopped
    ports:
      - ${MYSQL_PORT}:3306
    networks:
      - sakurachat-network
  phpmyadmin:
    image: phpmyadmin
    container_name: sakurachat-phpmyadmin
    depends_on:
      - mysql
    ports:
      - 8088:80
    environment:
      - PMA_ARBITRARY=1
    networks:
      - sakurachat-network
  backend:
    container_name: sakurachat-backend
    image: lovelycatv/sakurachat-backend:latest
    build:
      context: .
      dockerfile: Dockerfile
    depends_on:
      - mysql
    ports:
      - "${BACKEND_PORT}:8080"
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=-Duser.timezone=Asia/Shanghai
      - MYSQL_HOST=${MYSQL_HOST}
      - MYSQL_PORT=${MYSQL_PORT}
      - MYSQL_USERNAME=${MYSQL_USERNAME}
      - MYSQL_PASSWORD=${MYSQL_PASSWORD}
      - MYSQL_DATABASE=${MYSQL_DATABASE}
      - MYSQL_TIMEZONE=${MYSQL_TIMEZONE}
    restart: unless-stopped
    networks:
      - sakurachat-network
  frontend:
    container_name: sakurachat-frontend
    image: lovelycatv/sakurachat-frontend:latest
    build:
      context: ./web-dashboard
      dockerfile: Dockerfile
    ports:
      - "${FRONTEND_PORT}:80"
    restart: unless-stopped
    depends_on:
      - backend
    networks:
      - sakurachat-network
  napcat:
    image: mlikiowa/napcat-docker:latest
    container_name: sakurachat-napcat
    environment:
      - NAPCAT_UID=${NAPCAT_UID}
      - NAPCAT_GID=${NAPCAT_GID}
    ports:
      - "3000:3000"
      - "3001:3001"
      - "6099:6099"
    restart: always
    network_mode: bridge

networks:
  sakurachat-network:
    driver: bridge
```

```dotenv
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_USERNAME=root
MYSQL_PASSWORD=sakurachat
MYSQL_DATABASE=sakurachat
MYSQL_TIMEZONE=Asia/Shanghai

FRONTEND_PORT=5174
BACKEND_PORT=8080
```

详情请见：
[https://lovelycatv.com/cv1146]()

## 0x04 构建指南

### 前置要求
- **JDK 17+**：后端开发和运行环境
- **Node.js 18+**：前端开发和构建环境
- **Maven**：后端依赖管理
- **PNPM**：前端依赖管理

### 安装与运行

#### 1. 克隆仓库
```bash
git clone https://github.com/LovelyCatEx/SakuraChat.git
cd SakuraChat
```

#### 2. 后端配置与运行
```bash
# 进入后端目录
cd src

# 配置数据库连接
# 修改 src/main/resources/application.yml 文件中的数据库配置

# 构建项目
mvn clean package

# 运行项目
java -jar target/sakurachat-1.0.0.jar
```

#### 3. 前端配置与运行
```bash
# 进入前端目录
cd web-dashboard

# 安装依赖
pnpm install

# 运行开发服务器
pnpm dev

# 构建生产版本
pnpm build
```

## 0x05 贡献指南

我们欢迎社区贡献！如果你想为 SakuraChat 贡献代码，请按照以下步骤操作：

1. **Fork 仓库**：在 GitHub 上 Fork 本仓库到你的账号
2. **创建分支**：从 `master` 分支创建一个新的特性分支
3. **提交更改**：提交你的代码更改，并确保代码风格一致
4. **运行测试**：确保你的更改通过所有测试
5. **创建 Pull Request**：提交一个 PR 到本仓库的 `develop` 分支

## 0x06 许可证

本项目采用 Apache 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

---

**SakuraChat** - 遇见未来，与 AI 温柔对话

© 2026 SakuraChat AI (LovelyCat). 保留所有权利。