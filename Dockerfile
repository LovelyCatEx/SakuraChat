FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN apk add --no-cache git

COPY .mvn/ .mvn
COPY mvnw mvnw

RUN git clone https://github.com/LovelyCatEx/VertexLib.git /tmp/VertexLib && \
    cd /tmp/VertexLib && \
    chmod +x /app/mvnw && \
    /app/mvnw install -DskipTests

COPY pom.xml .
COPY src/ src/

RUN chmod +x ./mvnw && ./mvnw package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -jar $(find target -name '*.jar' | head -n 1)"]