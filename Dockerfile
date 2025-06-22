# --- STAGE 1: Build the Spring Boot application ---
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Install Maven
# apt-get update is necessary before apt-get install to get the latest package lists.
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy the Maven build files (pom.xml) first.
# This allows Docker to cache the dependencies layer if pom.xml doesn't change.
COPY pom.xml .

# Copy the source code.
# The .dockerignore file should exclude target/ and .idea/ etc.
COPY src ./src

# Build the Spring Boot application.
# The '-DskipTests' skips running tests during the Docker build.
# The 'install' goal creates the JAR in the 'target/' directory.
# For Gradle users, this line would be: RUN ./gradlew bootJar
RUN mvn clean install -DskipTests

# --- STAGE 2: Create the final lean image for running the application ---
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the final image
WORKDIR /app

# Copy the built JAR file from the 'builder' stage to the current stage.
# 'target/*.jar' will copy the fat JAR created by spring-boot-maven-plugin (e.g., my-spring-boot-app-0.0.1-SNAPSHOT.jar)
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your Spring Boot application runs on (default is 8080, but your application.yml uses 5000)
EXPOSE 5000

# Define the command to run the application when the container starts.
# Use 'java -jar' to execute the Spring Boot fat JAR.
# The '-Duser.timezone=UTC' ensures consistent timezone handling.
# The '-XX:InitialRAMPercentage', '-XX:MaxRAMPercentage' are good practices for memory management in containers.
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-XX:MaxMetaspaceSize=256m", "-Xmx512m", "-jar", "app.jar"]