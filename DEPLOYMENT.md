# 部署说明

本项目支持通过 Docker 和 Docker Compose 进行部署。以下是详细的部署步骤和说明。

## 环境要求

- Java 21
- Maven 3.6+
- Docker
- Docker Compose

## 构建应用

首先，确保你已经正确配置了 Java 21 环境变量 JAVA_HOME。

然后，使用 Maven 构建项目：

```bash
./mvnw clean package
```

## Docker 部署

### 1. 构建 Docker 镜像

```bash
docker build -t message-push-app .
```

### 2. 运行容器

```bash
docker run -d -p 8080:8080 --name message-push-container message-push-app
```

应用将在 `http://localhost:8080` 上运行。

## Docker Compose 部署

项目根目录下提供了 [docker-compose.yml](file:///Users/mac/Desktop/ideaProject/message_push/docker-compose.yml) 文件，可以简化部署流程。

### 1. 启动服务

```bash
docker-compose up -d
```

### 2. 停止服务

```bash
docker-compose down
```

## 注意事项

1. 如果你的环境中没有安装 Docker，请先安装 Docker Desktop 或相应的 Docker 引擎。
2. 如果遇到 Java 版本相关问题，请确保系统使用的 Java 版本为 Java 21。
3. 在生产环境中，建议将 Docker 镜像推送到镜像仓库，并在服务器上拉取部署。