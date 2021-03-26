FROM debian:buster-slim

RUN apt update \
            && apt install -y apt-transport-https \
                ca-certificates \
                wget \
                dirmngr \
                gnupg \
                software-properties-common \
                libatomic1

RUN wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add - \
            && add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/ \
            && mkdir -p /usr/share/man/man1 \
            && apt update \
            && apt install -y adoptopenjdk-8-hotspot


USER 1001