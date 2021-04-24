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
RUN groupadd --system --gid 1000 selve && useradd --no-log-init --system --uid 1000 -g selve selve
RUN mkdir /selve-bridge \
            && mkdir /selve-bridge/bin \
            && chown -R selve:selve /selve-bridge

COPY ./start.sh /selve-bridge/start.sh

WORKDIR /selve-bridge
ENTRYPOINT ["./start.sh"]
USER selve:selve