# Build for Private Blockchain CLI
# Define build arguments for version
ARG VERSION=1.0.3

FROM openjdk:21-jdk-slim AS builder
# Redefine ARG in the builder stage to make it available
ARG VERSION
LABEL maintainer="blockchain-team@company.com"
LABEL version="${VERSION}"
LABEL description="Private Blockchain CLI Builder"

# Install Maven and ZSH
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    maven \
    zsh && \
    rm -rf /var/lib/apt/lists/*

# Set ZSH as default shell
RUN chsh -s /usr/bin/zsh

# Set working directory for build
WORKDIR /build

# Copy Maven settings
RUN mkdir -p /root/.m2
COPY ./maven-settings.xml /root/.m2/settings.xml

# Prepare the Maven repository for the core dependency
RUN mkdir -p /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/

# Create POM file for the core dependency
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '    <modelVersion>4.0.0</modelVersion>' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '    <groupId>com.rbatllet</groupId>' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '    <artifactId>private-blockchain</artifactId>' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '    <version>${VERSION}</version>' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom && \
    echo '</project>' >> /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.pom

# Copy the core JAR file (this should be built and mounted during docker build)
COPY ./private-blockchain-${VERSION}.jar /root/.m2/repository/com/rbatllet/private-blockchain/${VERSION}/private-blockchain-${VERSION}.jar

# Copy CLI project files
COPY . .

# Build the project with Maven
RUN echo "🔨 Building project with Maven..." && \
    mvn clean package -DskipTests -Dmaven.test.skip=true

# Verify the CLI JAR was created
RUN ls -la target/*.jar && \
    echo "✅ CLI JAR built successfully" || \
    (echo "❌ CLI JAR build failed" && exit 1)

# Final stage: Create the runtime image
FROM openjdk:21-jdk-slim
# Redefine ARG in the final stage to make it available
ARG VERSION
LABEL maintainer="blockchain-team@company.com"
LABEL version="${VERSION}"
LABEL description="Private Blockchain CLI"

# Create non-root user for security
RUN groupadd -r blockchain && useradd -r -g blockchain blockchain

# Install ZSH
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    zsh && \
    rm -rf /var/lib/apt/lists/*

# Set ZSH as default shell for all users
RUN chsh -s /usr/bin/zsh root && \
    chsh -s /usr/bin/zsh blockchain

# Set working directory
WORKDIR /app

# Copy JAR files from builder stage
COPY --from=builder /build/target/*.jar /app/

# Rename the JAR to a standard name for easier reference
RUN find /app -name "*.jar" -type f -exec mv {} /app/blockchain-cli.jar \;

# List files in app directory
RUN ls -la /app/

# Create data directory and set permissions
RUN mkdir -p /app/data && \
    chown -R blockchain:blockchain /app

# Switch to non-root user
USER blockchain

# Set entrypoint and default command
ENTRYPOINT ["java", "-jar", "/app/blockchain-cli.jar"]
CMD ["--help"]
