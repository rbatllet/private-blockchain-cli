# 🐳 Docker Guide for Blockchain CLI

Complete guide for using the Private Blockchain CLI with Docker.

## 📋 Table of Contents

- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Basic Usage](#-basic-usage)
- [Docker Compose](#-docker-compose)
- [Volume Management](#-volume-management)
- [Production Usage](#-production-usage)
- [Troubleshooting](#-troubleshooting)

## 🚀 Quick Start

### Build and Run
```bash
# 1. Build the Docker image (one-time setup)
docker build -t blockchain-cli .

# 2. Basic usage - check if everything works
docker run --rm blockchain-cli --version
docker run --rm blockchain-cli status

# 3. Create data directory for persistent storage
mkdir -p blockchain-data backups

# 4. Run with persistent data (recommended)
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli status --detailed
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli validate

# 5. Add a test block with auto-generated key
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "My first Docker block" --generate-key

# 6. Search and export
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli search "Docker"
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli export /backups/my-backup.json
```

## 💻 Installation

### Prerequisites
- Docker installed and running
- At least 1GB free disk space

### Build Options

#### Option 1: Standard Build
```bash
docker build -t blockchain-cli .
```

#### Option 2: Multi-stage Build (smaller image)
```bash
docker build -t blockchain-cli:slim -f Dockerfile.slim .
```

#### Option 3: No-cache Build (for updates)
```bash
docker build --no-cache -t blockchain-cli .
```

## 🎯 Basic Usage

### Command Examples
```bash
# Version check
docker run --rm blockchain-cli --version

# Help
docker run --rm blockchain-cli --help

# Status without persistent data
docker run --rm blockchain-cli status

# Status with persistent data
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli status --detailed

# Add authorized key
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-key "Alice" --generate

# List keys
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli list-keys --detailed

# Add block
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "Hello Docker" --generate-key

# Validate chain
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli validate --detailed

# Search blocks
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli search "Hello"

# Export blockchain
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli export /backups/backup.json

# Import blockchain
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli import /backups/backup.json --validate-after
```

## 🎼 Docker Compose

### Basic Setup
```yaml
# docker-compose.yml
services:
  blockchain-cli:
    build: .
    container_name: blockchain-cli
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    environment:
      - JAVA_OPTS=-Xmx512m
      - TZ=Europe/Madrid
    command: ["status", "--detailed"]
    profiles: ["default"]
```

### Available Profiles

#### Default Profile
```bash
docker-compose --profile default up
```
Shows blockchain status with detailed information.

#### Validation Profile
```bash
docker-compose --profile validate up
```
Validates blockchain integrity and outputs JSON results.

#### Backup Profile
```bash
docker-compose --profile backup up
```
Creates timestamped backup automatically.

#### Interactive Profile
```bash
docker-compose --profile interactive up
```
Starts interactive shell for manual operations.

### Profile Configuration
```yaml
services:
  # Main service
  blockchain-cli:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    command: ["status", "--detailed"]
    profiles: ["default"]

  # Validator service
  blockchain-validator:
    build: .
    volumes:
      - ./blockchain-data:/data
    command: ["validate", "--detailed", "--json"]
    profiles: ["validate"]

  # Backup service
  blockchain-backup:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    entrypoint: ["sh", "-c"]
    command: ["java -jar /app/blockchain-cli.jar export /backups/backup_$$(date +%Y%m%d_%H%M%S).json"]
    profiles: ["backup"]

  # Interactive service
  blockchain-interactive:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    stdin_open: true
    tty: true
    entrypoint: ["/bin/bash"]
    profiles: ["interactive"]
```

## 📁 Volume Management

### Data Persistence
```bash
# Create persistent directories
mkdir -p blockchain-data backups logs

# Set proper permissions
chmod 755 blockchain-data backups logs

# Run with mounted volumes
docker run --rm \
  -v $(pwd)/blockchain-data:/data \
  -v $(pwd)/backups:/backups \
  -v $(pwd)/logs:/logs \
  blockchain-cli status --detailed
```

### Volume Backup
```bash
# Backup volume data
docker run --rm \
  -v $(pwd)/blockchain-data:/data \
  -v $(pwd)/volume-backups:/backup \
  alpine tar czf /backup/blockchain-data-$(date +%Y%m%d).tar.gz -C /data .

# Restore volume data
docker run --rm \
  -v $(pwd)/blockchain-data:/data \
  -v $(pwd)/volume-backups:/backup \
  alpine tar xzf /backup/blockchain-data-20250611.tar.gz -C /data
```

### Named Volumes
```yaml
# docker-compose.yml with named volumes
services:
  blockchain-cli:
    build: .
    volumes:
      - blockchain_data:/data
      - backup_data:/backups
    command: ["status"]

volumes:
  blockchain_data:
    driver: local
  backup_data:
    driver: local
```

## 🏭 Production Usage

### Production Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
RUN groupadd -r blockchain && useradd -r -g blockchain blockchain
WORKDIR /app
COPY --from=builder /app/target/blockchain-cli.jar .
RUN mkdir -p /data && chown blockchain:blockchain /data
VOLUME ["/data"]
USER blockchain
WORKDIR /data
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD timeout 8s java -jar /app/blockchain-cli.jar status >/dev/null 2>&1 || exit 1
ENTRYPOINT ["java", "-jar", "/app/blockchain-cli.jar"]
CMD ["--help"]
```

### Production Docker Compose
```yaml
version: '3.8'
services:
  blockchain:
    build: .
    restart: unless-stopped
    volumes:
      - ./data:/data
      - ./backups:/backups
      - ./logs:/logs
    environment:
      - JAVA_OPTS=-Xmx1024m -XX:+UseG1GC
      - TZ=UTC
    healthcheck:
      test: ["CMD", "java", "-jar", "/app/blockchain-cli.jar", "status"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  backup:
    build: .
    volumes:
      - ./data:/data
      - ./backups:/backups
    entrypoint: ["sh", "-c"]
    command: |
      "while true; do
        java -jar /app/blockchain-cli.jar export /backups/auto_backup_$$(date +%Y%m%d_%H%M%S).json
        find /backups -name 'auto_backup_*.json' -mtime +7 -delete
        sleep 86400
      done"
    restart: unless-stopped
    profiles: ["backup"]

networks:
  default:
    driver: bridge
```

### Environment Variables
```bash
# Runtime configuration
export JAVA_OPTS="-Xmx2048m -XX:+UseG1GC"
export BLOCKCHAIN_DATA_DIR="/data"
export BACKUP_RETENTION_DAYS="30"

# Run with environment
docker run --rm \
  -e JAVA_OPTS="$JAVA_OPTS" \
  -e TZ="Europe/Madrid" \
  -v $(pwd)/blockchain-data:/data \
  blockchain-cli status --detailed
```

### Resource Limits
```yaml
services:
  blockchain-cli:
    build: .
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1024M
        reservations:
          cpus: '0.5'
          memory: 512M
    ulimits:
      nofile:
        soft: 65536
        hard: 65536
```

## 🔧 Container Management

### Container Lifecycle
```bash
# Stop containers safely
docker ps -q --filter ancestor=blockchain-cli | xargs -r docker stop

# Remove containers
docker ps -aq --filter ancestor=blockchain-cli | xargs -r docker rm

# Remove image
docker rmi blockchain-cli

# Clean up everything
docker-compose down --volumes --remove-orphans
docker system prune -f
```

### Health Monitoring
```bash
# Check container health
docker ps --filter ancestor=blockchain-cli

# View container logs
docker logs container_name

# Execute commands in running container
docker exec -it container_name java -jar /app/blockchain-cli.jar status

# Monitor resource usage
docker stats container_name
```

## 🛠️ Troubleshooting

### Common Issues

#### Issue: "Cannot connect to the Docker daemon"
```bash
# Start Docker service
sudo systemctl start docker

# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker
```

#### Issue: "Build fails with Java errors"
```bash
# Check Java version in container
docker run --rm openjdk:21-jdk-slim java -version

# Clean build
docker build --no-cache -t blockchain-cli .

# Check available space
docker system df
```

#### Issue: "Container exits immediately"
```bash
# Check container logs
docker logs container_name

# Run with interactive mode
docker run -it blockchain-cli /bin/bash

# Test basic command
docker run --rm blockchain-cli --version
```

#### Issue: "Volume permissions"
```bash
# Fix permissions
sudo chown -R $USER:$USER blockchain-data backups

# Or run as specific user
docker run --rm --user $(id -u):$(id -g) \
  -v $(pwd)/blockchain-data:/data \
  blockchain-cli status
```

#### Issue: "Out of disk space"
```bash
# Clean Docker system
docker system prune -a

# Remove unused volumes
docker volume prune

# Check disk usage
docker system df
df -h
```

### Debug Mode
```bash
# Verbose output
docker run --rm blockchain-cli --verbose status

# Shell access for debugging
docker run -it --entrypoint=/bin/bash blockchain-cli

# Check container filesystem
docker run --rm blockchain-cli ls -la /app
docker run --rm blockchain-cli ls -la /data
```

### Performance Optimization
```bash
# Increase memory limit
docker run --rm -m 2g blockchain-cli status

# Use specific JVM options
docker run --rm \
  -e JAVA_OPTS="-Xmx1024m -XX:+UseG1GC" \
  blockchain-cli status

# Monitor performance
docker stats --no-stream container_name
```

## 🔗 Related Documents

- [Main README](README.md) - Getting started
- [Examples](EXAMPLES.md) - Practical use cases
- [Troubleshooting](TROUBLESHOOTING.md) - General troubleshooting
- [Enterprise Guide](ENTERPRISE_GUIDE.md) - Production best practices

---

**Need help?** Check the main [Troubleshooting Guide](TROUBLESHOOTING.md) or return to the [README](README.md).
