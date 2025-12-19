# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:21-jdk-slim

# 安装 Maven
RUN apt-get update && apt-get install -y maven

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 暴露应用端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "target/message_push-0.0.1-SNAPSHOT.jar"]