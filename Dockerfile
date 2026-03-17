# Etapa 1: build
FROM openjdk:25-jdk-slim AS build

WORKDIR /app

# Copia só o pom e mvnw primeiro
COPY pom.xml mvnw ./
RUN chmod +x mvnw

# Resolve dependências (cache)
RUN ./mvnw dependency:resolve

# Agora copia o código-fonte
COPY src ./src

# Build do jar sem testes
RUN ./mvnw clean package -DskipTests


# Etapa 2: execução
FROM openjdk:25-jdk-slim

WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/ligafacil-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]