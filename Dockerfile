# ---------- Estagio 1: build (compila e empacota com Maven) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cacheia dependencias: copia apenas o pom primeiro
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# Copia o codigo-fonte e gera o jar (testes rodam no pipeline, nao na imagem)
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---------- Estagio 2: runtime (imagem enxuta, apenas JRE) ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Fuso horario padrao da aplicacao: UTC
ENV TZ=UTC

# Usuario nao-root por seguranca
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/biblioteca-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]
