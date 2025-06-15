# Simple production image using pre-built JAR
FROM openjdk:21-jdk-slim
LABEL maintainer="blockchain-team@company.com"
LABEL version="1.0.1"
LABEL description="Private Blockchain CLI"

# Create non-root user for security
RUN groupadd -r blockchain && useradd -r -g blockchain blockchain

# Install runtime dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Copy pre-built JAR
WORKDIR /app
COPY target/blockchain-cli.jar .

# Create data directory and set permissions
RUN mkdir -p /data && chown blockchain:blockchain /data
VOLUME ["/data"]

# Switch to non-root user
USER blockchain
WORKDIR /data

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD timeout 8s java -jar /app/blockchain-cli.jar status >/dev/null 2>&1 || exit 1

# Set entrypoint and default command
ENTRYPOINT ["java", "-jar", "/app/blockchain-cli.jar"]
CMD ["--help"]
