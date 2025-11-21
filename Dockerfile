FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper files
COPY .mvn/ .mvn
COPY mvnw .
RUN chmod +x mvnw

# Copy project files
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy all source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the Spring Boot JAR
CMD ["java", "-jar", "target/complaintsystem-1.0.0.jar"]
