# Multi-stage build for React frontend
FROM node:18-alpine as frontend-build

# Set working directory for frontend
WORKDIR /app/frontend

# Copy package files
COPY frontend/package*.json ./

# Install dependenciess
RUN npm ci

# Copy frontend source code
COPY frontend/ ./

# Build React app
RUN npm run build

# Backend build stage
FROM openjdk:17-jdk-slim as backend-build

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/build.gradle.kts .
COPY backend/settings.gradle.kts .
COPY backend/gradle.properties .

# Copy source code
COPY backend/src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Final stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy built backend from backend-build stage
COPY --from=backend-build /app/build/libs/*.jar app.jar

# Copy built frontend from frontend-build stage
COPY --from=frontend-build /app/frontend/build /app/static

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]