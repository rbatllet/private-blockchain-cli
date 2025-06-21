# 🐳 Docker Guide for Private Blockchain CLI

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
```zsh
#!/usr/bin/env zsh

# Define default values
VERSION="latest"
DATA_DIR="$(pwd)/blockchain-data"
BACKUP_DIR="$(pwd)/backups"

# Create directories if they don't exist
mkdir -p "$DATA_DIR" "$BACKUP_DIR"

# Run the Docker container with the provided arguments
docker run --rm \
  -v "$DATA_DIR":/app/data \
  -v "$BACKUP_DIR":/backups \
  --entrypoint /bin/zsh \
  private-blockchain-cli:"$VERSION" \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar $*"
```

### Example Usage
```zsh
# 1. Clone the repository
git clone https://github.com/rbatllet/privateBlockchain-cli.git
cd privateBlockchain-cli

# 2. Build the Docker image
./build-docker.sh

# 3. Create directories for persistent data
mkdir -p blockchain-data backups

# 4. Run with persistent data (recommended)
./run-docker.sh status --detailed
./run-docker.sh validate

# 5. Add a test block with auto-generated key
./run-docker.sh add-block "My first Docker block" --generate-key

# 6. Search and export
./run-docker.sh search "Docker"
./run-docker.sh export /backups/my-backup.json
```

## 🔰 Docker Basics for Beginners

If you're new to Docker, here are some essential commands to help you manage the blockchain Docker container:

### Building and Rebuilding the Image

```zsh
# Build the Docker image for the first time
./build-docker.sh

# Rebuild the image (useful after code changes)
./build-docker.sh --force

# Build with a specific version tag
./build-docker.sh 1.0.3
```

### Managing Containers

```zsh
# List all running containers
docker ps

# List all containers (including stopped ones)
docker ps -a

# Stop a specific container
docker stop CONTAINER_ID

# Stop all running blockchain containers
docker ps -q --filter ancestor=private-blockchain-cli | xargs -r docker stop

# Remove a stopped container
docker rm CONTAINER_ID

# Remove all stopped containers
docker container prune
```

### Managing Images

```zsh
# List all Docker images
docker images

# Remove a specific image
docker rmi private-blockchain-cli:latest

# Remove all unused images
docker image prune
```

### Troubleshooting

```zsh
# View logs of a running container
docker logs CONTAINER_ID

# Access a running container's shell
docker exec -it CONTAINER_ID /bin/zsh

# Check Docker system information
docker info

# Check Docker disk usage
docker system df
```

### Data Management

```zsh
# Create directories for persistent data
mkdir -p blockchain-data backups

# Check contents of the data directory
ls -la blockchain-data/

# Back up the data directory
cp -r blockchain-data/ blockchain-data-backup-$(date +%Y%m%d)
```

## 💻 Installation

### Prerequisites
- Docker installed and running
- At least 1GB free disk space
- Java JDK 21 (only needed if building the core project)
- Maven (only needed if building the core project)

### Build Options

#### Option 1: Using the Build Script (Recommended)
The project includes a build script that automatically handles the core project dependency and builds the Docker image:

```zsh
# Build with default version (1.0.3)
./build-docker.sh

# Build with specific version
./build-docker.sh 1.0.3
```

The script will:
1. Check if the specified version of the core project exists in your local Maven repository
2. If not found, offer to build and install it automatically
3. Build the Docker image with the correct dependencies

#### Option 2: Manual Build
If you prefer to build manually, ensure the core project JAR is available in your local Maven repository first:

```zsh
# First, build and install the core project
cd ../privateBlockchain
mvn clean install -DskipTests

# Then copy the JAR and build the Docker image
cd ../privateBlockchain-cli
cp ~/.m2/repository/com/rbatllet/private-blockchain/1.0.3/private-blockchain-1.0.3.jar .
docker build -t private-blockchain-cli:1.0.3 .
```

#### Option 3: No-cache Build (for updates)
```zsh
./build-docker.sh --no-cache
```

## 🎯 Basic Usage

### Command Examples
```zsh
# Version check
docker run --rm private-blockchain-cli:latest --version

# Help
docker run --rm private-blockchain-cli:latest --help

# Status without persistent data
docker run --rm private-blockchain-cli:latest status

# Status with persistent data
./run-docker.sh status --detailed

# Add authorized key
./run-docker.sh add-key "Alice" --generate

# List keys
./run-docker.sh list-keys --detailed

# Add block
./run-docker.sh add-block "Hello Docker" --generate-key

# Validate chain
./run-docker.sh validate --detailed

# Search blocks
./run-docker.sh search "Hello"

# Export blockchain
./run-docker.sh export /backups/my-backup.json

# Import blockchain
./run-docker.sh import /backups/my-backup.json --validate-after
```

### Using Different Versions
You can specify which version of the image to use:

```zsh
# Using a specific version
docker run --rm private-blockchain-cli:1.0.3 --version

# Using the latest version (recommended for most cases)
docker run --rm private-blockchain-cli:latest --version

# Using an environment variable to specify version
VERSION=1.0.3 docker run --rm private-blockchain-cli:$VERSION --version
```

## 🎼 Docker Compose

### Basic Setup
```yaml
# docker-compose.yml
services:
  private-blockchain-cli:
    image: private-blockchain-cli:${VERSION:-latest}
    container_name: private-blockchain-cli
    volumes:
      - ./blockchain-data:/app/data
      - ./backups:/backups
    environment:
      - JAVA_OPTS=-Xmx512m
      - TZ=Europe/Madrid
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status --detailed"]
    profiles: ["default"]
```

### Using Environment Variables

The docker-compose.yml file uses environment variables to avoid hardcoded versions:

```zsh
# Run with default version (latest)
docker-compose up

# Run with specific version
VERSION=1.0.3 docker-compose up

# Run with specific version and profile
VERSION=1.0.3 docker-compose --profile validate up
```

### Available Profiles

#### Default Profile
```zsh
docker-compose --profile default up
```
Shows blockchain status with detailed information.

#### Validation Profile
```zsh
docker-compose --profile validate up
```
Validates blockchain integrity and outputs JSON results.

#### Backup Profile
```zsh
docker-compose --profile backup up
```
Creates timestamped backup automatically.

#### Interactive Profile
```zsh
docker-compose --profile interactive up
```
Starts interactive shell for manual operations.

### Profile Configuration
```yaml
services:
  # Main service
  private-blockchain-cli:
    image: private-blockchain-cli:${VERSION:-latest}
    volumes:
      - ./blockchain-data:/app/data
      - ./backups:/backups
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status --detailed"]
    profiles: ["default"]

  # Validator service
  private-blockchain-validator:
    image: private-blockchain-cli:${VERSION:-latest}
    volumes:
      - ./blockchain-data:/app/data
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar validate --detailed --json"]
    profiles: ["validate"]

  # Backup service
  private-blockchain-backup:
    image: private-blockchain-cli:${VERSION:-latest}
    volumes:
      - ./blockchain-data:/app/data
      - ./backups:/backups
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar export /backups/backup_$$(date +%Y%m%d_%H%M%S).json"]
    profiles: ["backup"]

  # Interactive service
  private-blockchain-interactive:
    image: private-blockchain-cli:${VERSION:-latest}
    volumes:
      - ./blockchain-data:/app/data
      - ./backups:/backups
    stdin_open: true
    tty: true
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && /bin/zsh"]
    profiles: ["interactive"]
```

## 📁 Volume Management

### Data Persistence
```zsh
# Create persistent directories
mkdir -p blockchain-data backups logs

# Set proper permissions
chmod 755 blockchain-data backups logs

# Run with mounted volumes
docker run --rm \
  -v $(pwd)/blockchain-data:/app/data \
  -v $(pwd)/backups:/backups \
  -v $(pwd)/logs:/logs \
  --entrypoint /bin/zsh \
  private-blockchain-cli:latest \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status --detailed"
```

### Volume Backup

```zsh
# Create a backup of the blockchain data
docker run --rm \
  -v $(pwd)/blockchain-data:/app/data \
  -v $(pwd)/backups:/backups \
  --entrypoint /bin/zsh \
  private-blockchain-cli:latest \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar export /backups/backup_$(date +%Y%m%d_%H%M%S).json"
```
```zsh
# Backup volume data
docker run --rm \
  -v $(pwd)/blockchain-data:/app/data \
  -v $(pwd)/volume-backups:/backup \
  alpine tar czf /backup/blockchain-data-$(date +%Y%m%d).tar.gz -C /app/data .

# Restore volume data
docker run --rm \
  -v $(pwd)/blockchain-data:/app/data \
  -v $(pwd)/volume-backups:/backup \
  alpine tar xzf /backup/blockchain-data-20250611.tar.gz -C /app/data
```

### Named Volumes
```yaml
# docker-compose.yml with named volumes
services:
  blockchain-cli:
    build: .
    volumes:
      - blockchain_data:/app/data
      - backup_data:/backups
    entrypoint: ["/bin/zsh", "-c"]
    command: ["cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status"]

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
```zsh
# Set Java options
JAVA_OPTS="-Xmx512m -Duser.timezone=UTC"

# Run with environment variables
docker run --rm \
  -e JAVA_OPTS="$JAVA_OPTS" \
  -e TZ="Europe/Madrid" \
  -v $(pwd)/blockchain-data:/app/data \
  --entrypoint /bin/zsh \
  blockchain-cli \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status --detailed"
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
```zsh
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
```zsh
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
```zsh
# Start Docker service
sudo systemctl start docker

# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker
```

#### Issue: "Build fails with Java errors"
```zsh
# Check Java version in container
docker run --rm openjdk:21-jdk-slim java -version

# Clean build
docker build --no-cache -t blockchain-cli .

# Check available space
docker system df
```

#### Issue: "Container exits immediately"
```zsh
# Check container logs
docker logs container_name

# Run with interactive mode
docker run -it blockchain-cli /bin/zsh

# Test basic command
docker run --rm blockchain-cli --version
```

#### Issue: "Volume permissions"
```zsh
# Run as current user
docker run --rm --user $(id -u):$(id -g) \
  -v $(pwd)/blockchain-data:/app/data \
  --entrypoint /bin/zsh \
  private-blockchain-cli:latest \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar status"
```

#### Issue: "Out of disk space"
```zsh
# Clean Docker system
docker system prune -a

# Remove unused volumes
docker volume prune

# Check disk usage
docker system df
df -h
```

### Debug Mode
```zsh
# Verbose output
docker run --rm blockchain-cli --verbose status

# Shell access for debugging
docker run -it --entrypoint=/bin/zsh blockchain-cli

# Check container filesystem
docker run --rm blockchain-cli ls -la /app
docker run --rm blockchain-cli ls -la /data
```

### Performance Optimization
```zsh
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
