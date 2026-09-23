FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /workspace

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean bootJar --no-daemon

RUN set -eu; \
    jar_file="$(find build/libs \
        -maxdepth 1 \
        -type f \
        -name '*.jar' \
        ! -name '*-plain.jar' \
        -print \
        -quit)"; \
    test -n "$jar_file"; \
    cp "$jar_file" /workspace/app.jar


FROM eclipse-temurin:21-jre-noble

RUN groupadd \
        --gid 10001 \
        templebar \
    && useradd \
        --uid 10001 \
        --gid 10001 \
        --create-home \
        --home-dir /home/templebar \
        --shell /usr/sbin/nologin \
        templebar

WORKDIR /app

RUN mkdir -p /data/posters \
    && chown -R \
        templebar:templebar \
        /app \
        /data/posters

COPY \
    --from=build \
    --chown=templebar:templebar \
    /workspace/app.jar \
    /app/app.jar

USER 10001:10001

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV STORAGE_UPLOAD_DIRECTORY=/data/posters

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
