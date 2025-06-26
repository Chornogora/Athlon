# --- STAGE 1: Build the Spring Boot application ---
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Install Maven
# apt-get update is necessary before apt-get install to get the latest package lists.
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy the Maven build files (pom.xml).
COPY pom.xml .

# Copy the source code.
COPY src ./src

# Build the Spring Boot application.
RUN mvn clean install -DskipTests

# --- STAGE 2: Create the final lean image for running the application ---
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the final image
WORKDIR /app

# Copy the built JAR file from the 'builder' stage to the current stage.
# 'target/*.jar' will copy the fat JAR created by spring-boot-maven-plugin (e.g., my-spring-boot-app-0.0.1-SNAPSHOT.jar)
COPY --from=builder /app/target/*.jar app.jar

# Download ffmpeg
RUN apt-get update && \
    apt-get install -y --no-install-recommends ffmpeg && \
    rm -rf /var/lib/apt/lists/*

#create directories for file downloading/converting
RUN mkdir /temp
RUN mkdir /temp/input
RUN mkdir /temp/output

# Expose the port the Spring Boot application runs
EXPOSE 5000

# Define the command to run the application when the container starts.
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-XX:MaxMetaspaceSize=256m", "-Xmx512m", "-jar", "app.jar"]