#!/usr/bin/env zsh

# Define default values
VERSION="latest"
DATA_DIR="$(pwd)/blockchain-data"
BACKUP_DIR="$(pwd)/backups"

# Create directories if they don't exist
mkdir -p "$DATA_DIR" "$BACKUP_DIR"

# Prepare the command with proper escaping
ARGS_ESCAPED=""
for arg in "$@"; do
  ARGS_ESCAPED="$ARGS_ESCAPED \"$arg\""
done

# Run the Docker container with the provided arguments
docker run --rm \
  -v "$DATA_DIR":/app/data \
  -v "$BACKUP_DIR":/backups \
  --entrypoint /bin/zsh \
  private-blockchain-cli:"$VERSION" \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar$ARGS_ESCAPED"
