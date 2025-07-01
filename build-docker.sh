#!/usr/bin/env zsh
# Script to build the Docker image for Private Blockchain CLI using a specific core version

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default version if not specified
DEFAULT_VERSION="1.0.4"
VERSION=${1:-$DEFAULT_VERSION}

# Ensure VERSION is not empty
if [ -z "$VERSION" ]; then
  echo -e "${RED}Error: Version cannot be empty${NC}"
  exit 1
fi

MAVEN_REPO="$HOME/.m2/repository"

echo -e "${BLUE}Building Private Blockchain CLI Docker Image${NC}"
echo -e "${YELLOW}=====================================${NC}"
echo -e "${BLUE}Using core version: ${GREEN}$VERSION${NC}"

# Step 1: Check if the core JAR exists in the local Maven repository or other locations
echo -e "${BLUE}Step 1: Checking for core JAR...${NC}"

# Define possible locations for the core JAR
CORE_JAR_PATH="$MAVEN_REPO/com/rbatllet/private-blockchain/$VERSION/private-blockchain-$VERSION.jar"
CORE_JAR_WITH_DEP_PATH="$MAVEN_REPO/com/rbatllet/private-blockchain/$VERSION/private-blockchain-$VERSION-jar-with-dependencies.jar"
CORE_PROJECT_JAR_PATH="../privateBlockchain/target/private-blockchain-$VERSION.jar"
CORE_PROJECT_JAR_WITH_DEP_PATH="../privateBlockchain/target/private-blockchain-$VERSION-jar-with-dependencies.jar"

# Check all possible locations
if [ -f "$CORE_JAR_WITH_DEP_PATH" ]; then
  echo -e "${GREEN}Found core JAR with dependencies in local Maven repository${NC}"
  CORE_JAR="$CORE_JAR_WITH_DEP_PATH"
elif [ -f "$CORE_JAR_PATH" ]; then
  echo -e "${GREEN}Found core JAR in local Maven repository${NC}"
  CORE_JAR="$CORE_JAR_PATH"
elif [ -f "$CORE_PROJECT_JAR_WITH_DEP_PATH" ]; then
  echo -e "${GREEN}Found core JAR with dependencies in project target directory${NC}"
  CORE_JAR="$CORE_PROJECT_JAR_WITH_DEP_PATH"
elif [ -f "$CORE_PROJECT_JAR_PATH" ]; then
  echo -e "${GREEN}Found core JAR in project target directory${NC}"
  CORE_JAR="$CORE_PROJECT_JAR_PATH"
else
  echo -e "${YELLOW}Core JAR version $VERSION not found in local Maven repository${NC}"
  echo -e "${YELLOW}Please build and install the core project first with:${NC}"
  echo -e "${BLUE}cd ../privateBlockchain && mvn clean install -DskipTests${NC}"
  
  # Ask if user wants to build it now
  read -p "Do you want to build and install the core project now? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Building core project...${NC}"
    cd ../privateBlockchain
    if [ $? -ne 0 ]; then
      echo -e "${RED}Error: Could not find the privateBlockchain directory${NC}"
      echo -e "${YELLOW}Make sure the privateBlockchain project is in the same parent directory${NC}"
      exit 1
    fi
    
    echo -e "${GREEN}Building and installing core project with Maven...${NC}"
    mvn install
    if [ $? -ne 0 ]; then
      echo -e "${RED}Error: Failed to build core project${NC}"
      exit 1
    fi
    
    # Check again for the JAR
    if [ -f "$CORE_JAR_WITH_DEP_PATH" ]; then
      echo -e "${GREEN}Found core JAR with dependencies in local Maven repository${NC}"
      CORE_JAR="$CORE_JAR_WITH_DEP_PATH"
    elif [ -f "$CORE_JAR_PATH" ]; then
      echo -e "${GREEN}Found core JAR in local Maven repository${NC}"
      CORE_JAR="$CORE_JAR_PATH"
    else
      echo -e "${RED}Error: Still could not find core JAR after building${NC}"
      exit 1
    fi
    
    # Navigate back to CLI directory
    cd ../privateBlockchain-cli
    if [ $? -ne 0 ]; then
      echo -e "${RED}Error: Could not navigate back to CLI project directory${NC}"
      exit 1
    fi
  else
    echo -e "${RED}Cannot proceed without the core JAR${NC}"
    exit 1
  fi
fi

# Step 2: Copy the JAR to the CLI project directory
echo -e "${BLUE}Step 2: Copying JAR to CLI project...${NC}"
echo -e "${GREEN}Using core JAR: $CORE_JAR${NC}"
cp "$CORE_JAR" "./private-blockchain-$VERSION.jar"
if [ $? -ne 0 ]; then
  echo -e "${RED}Error: Failed to copy core JAR to CLI project${NC}"
  exit 1
fi

# Step 3: Build the Docker image
echo -e "${BLUE}Step 3: Building Docker image...${NC}"

# Create a temporary Dockerfile with the correct version
echo -e "${GREEN}Creating temporary Dockerfile with version $VERSION...${NC}"
cp Dockerfile Dockerfile.template
sed "s/\${VERSION}/$VERSION/g" Dockerfile.template > Dockerfile.tmp
if [ $? -ne 0 ]; then
  echo -e "${RED}Error: Could not create temporary Dockerfile${NC}"
  rm -f Dockerfile.template
  exit 1
fi

echo -e "${GREEN}Building Docker image...${NC}"
docker build -f Dockerfile.tmp -t private-blockchain-cli:$VERSION .
if [ $? -ne 0 ]; then
  echo -e "${RED}Error: Docker build failed${NC}"
  # Clean up temporary files
  rm -f Dockerfile.tmp Dockerfile.template
  exit 1
fi

echo -e "${GREEN}Docker image built successfully!${NC}"

# Also tag as latest for convenience
echo -e "${BLUE}Creating 'latest' tag...${NC}"
docker tag private-blockchain-cli:$VERSION private-blockchain-cli:latest
if [ $? -ne 0 ]; then
  echo -e "${YELLOW}Warning: Could not create 'latest' tag${NC}"
else
  echo -e "${GREEN}Successfully created 'latest' tag${NC}"
fi

echo -e "${YELLOW}You can run the container with:${NC}"
echo -e "${BLUE}docker run -it private-blockchain-cli:$VERSION${NC}"
echo -e "${BLUE}or${NC}"
echo -e "${BLUE}docker run -it private-blockchain-cli:latest${NC}"

# Clean up
echo -e "${BLUE}Cleaning up...${NC}"
rm -f "private-blockchain-$VERSION.jar" Dockerfile.tmp Dockerfile.template
echo -e "${GREEN}Done!${NC}"

echo -e "${YELLOW}Note: You can specify a different version when running this script:${NC}"
echo -e "${BLUE}./build-docker.sh 1.0.4${NC}"
