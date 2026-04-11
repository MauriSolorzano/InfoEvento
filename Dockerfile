# Etapa 1: Compilación (Build)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Copiamos el pom y descargamos dependencias para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos solo el jar resultante de la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto (ajustalo si no usás el 8080)
EXPOSE 8080
# Comando para arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]