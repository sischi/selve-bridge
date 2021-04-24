#!/bin/bash


docker build -t selve-bridge:latest .

docker run -d \
        -p 8080:8080 \
        -v $(pwd)/bin:/selve-bridge/bin \
        -v $(pwd)/config:/selve-bridge/config \
        -e TZ=Europe/Berlin \
        --device=$(readlink -f /dev/selve-gateway):/dev/selve-gateway \
        --name selve-bridge \
        selve-bridge:latest



