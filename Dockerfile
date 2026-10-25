# Build stage: the Maven Wrapper downloads its own Maven distribution, so a plain JDK image is
# enough (no need for a maven:* base image).
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q clean package -DskipTests

# Runtime stage: unprivileged user; the heap follows the container memory limit.
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN groupadd --system bladi && useradd --system --gid bladi --home-dir /app bladi \
    && mkdir -p /app/data/documents /app/logs && chown -R bladi:bladi /app
COPY --from=build --chown=bladi:bladi /app/target/*.jar app.jar
USER bladi

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
