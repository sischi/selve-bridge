#!/bin/bash


docker build -t adoptopenjdk-8:latest .

docker run -d \
        -p 8080:8080 \
        -v $(pwd)/app:/app \
        --device=$(readlink -f /dev/selve-gateway):/dev/selve-gateway \
        --workdir /app \
        --entrypoint="/app/start.sh" \
        --name selve-bridge \
        adoptopenjdk-8



