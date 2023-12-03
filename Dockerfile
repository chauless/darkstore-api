FROM maven:3.8.6-openjdk-18-slim
LABEL author="Matvei Morenkov, Maksym Bahmet"

WORKDIR /app

# COPY pom.xml
COPY pom.xml .
RUN mvn clean package -DskipTests

# COPY source code
COPY target/DarkstoreApi-0.0.1-SNAPSHOT.jar /app/darkstore-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "darkstore-api.jar"]
