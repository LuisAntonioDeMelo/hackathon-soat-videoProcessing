# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar arquivos de configuração Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Download dependencies
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src src

# Build da aplicação
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Criar usuário não-root
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

# Instalar FFmpeg para processamento de vídeo
RUN apk add --no-cache ffmpeg

# Criar diretórios necessários
RUN mkdir -p /app/uploads /app/outputs && \
    chown -R spring:spring /app

# Copiar JAR da aplicação
COPY --from=builder /app/target/*.jar app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para iniciar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
