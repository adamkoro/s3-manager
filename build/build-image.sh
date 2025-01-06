#!/bin/bash
DOCKER_USERNAME="adamkoro"
FRONTEND_IMAGE_NAME="s3-manager-frontend"
BACKEND_IMAGE_NAME="s3-manager-backend"

VERSION="latest"
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

if ! docker info > /dev/null 2>&1; then
    error "Docker is not running. Please start Docker first."
fi

log "Building Frontend..."
cd frontend || error "Frontend directory not found"

log "Building Nuxt Docker image..."
docker build -t "${DOCKER_USERNAME}/${FRONTEND_IMAGE_NAME}:${VERSION}" .

log "Pushing frontend image to DockerHub..."
docker push "${DOCKER_USERNAME}/${FRONTEND_IMAGE_NAME}:${VERSION}"

log "Building Backend..."
cd ../backend || error "Backend directory not found"

log "Building with Maven and creating Docker image..."
./mvnw clean install spring-boot:build-image -DskipTests -Dimage.name=${DOCKER_USERNAME}/${BACKEND_IMAGE_NAME}:${VERSION}

log "Pushing backend image to DockerHub..."
docker push "${DOCKER_USERNAME}/${BACKEND_IMAGE_NAME}:${VERSION}"

log "Build and push completed successfully!"
