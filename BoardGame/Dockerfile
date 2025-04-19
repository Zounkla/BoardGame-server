FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests


FROM build AS dev
WORKDIR /app
COPY . .
ENTRYPOINT mvn spring-boot:run
