FROM eclipse-temurin:21-jre-alpine

# 2. 设置工作目录
WORKDIR /app

COPY target/*.jar app.jar

# 4. 暴露端口
EXPOSE 9001

# 5. 启动
ENTRYPOINT ["java", "-jar", "app.jar"]