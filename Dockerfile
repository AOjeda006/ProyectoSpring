# =====================================================================
# Etapa 1 - Construcción del JAR con Maven (usando el wrapper del repo)
# =====================================================================
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia primero los descriptores para aprovechar la caché de capas
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copia el código y empaqueta (los tests se ejecutan en CI, no en la imagen)
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# =====================================================================
# Etapa 2 - Imagen de ejecución ligera (solo JRE + el JAR)
# =====================================================================
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Usuario no root por seguridad
RUN useradd --system --no-create-home spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
