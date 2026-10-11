FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/server-all.jar app.jar

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-Xms64m -Xmx192m -XX:MaxDirectMemorySize=64m"

ENTRYPOINT ["java", "-jar", "app.jar"]
