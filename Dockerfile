# Etapa 1: build

# Imagem base pro build
FROM openjdk:21-jdk-slim AS build

# Diretorio de trabalho
WORKDIR /app

# Copia só o pom e mvnw primeiro
COPY pom.xml mvnw ./

# Permissao pra executar o mvn wrapper
RUN chmod +x mvnw

# Resolve dependências (cache)
RUN ./mvnw dependency:resolve

# Copia o codigo fonte
COPY src ./src

# Build do jar sem testes
RUN ./mvnw clean package -DskipTests


# Etapa 2: execução
FROM openjdk:21-jdk-slim

# Diretorio de trabalho no container final
WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/ligafacil-0.0.1-SNAPSHOT.jar app.jar

# Expoe a porta usada
EXPOSE 8080

# Comando que vai rodar quando container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]