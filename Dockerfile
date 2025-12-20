# Step 1: Use an official Maven image with JDK 17
FROM maven:3.9.6-eclipse-temurin-17

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy your pom.xml and source code into the container
COPY . .

# Step 4: Run a command to download dependencies (Optimization)
RUN mvn dependency:go-offline

# Step 5: Default command (This is what runs when the container starts)
CMD ["mvn", "test"]