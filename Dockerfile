# Use Java 17 runtime
FROM eclipse-temurin:17-jdk

# Set working directory in the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Download all dependencies (this will speed up future builds)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/complaintsystem-1.0.0.jar"]
