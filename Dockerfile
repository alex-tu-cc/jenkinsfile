FROM ubuntu:bionic
LABEL MAINTAINER="Alex Tu <alextu@cctu.space>"

# this image is assumed to be run with -h oem-taipei-bot

ARG user=oem-taipei-bot
ARG group=oem-taipei-bot
ARG uid=1000
ARG gid=1000
ARG HOME=/home/${user}

ENV USER ${user}
ENV HOME ${HOME}
ENV DEBFULLNAME ${user}
ENV DEBEMAIL="${user}@canonical.com"
ENV DEBIAN_FRONTEND noninteractive

RUN echo "deb [ trusted=yes ] http://ppa.launchpad.net/alextu/pc-tools/ubuntu bionic main" > /etc/apt/sources.list.d/pre-install.list \
    && apt-get update \
    && apt-get install --no-install-recommends -y gnupg ca-certificates bzr git sudo python python3 software-properties-common lp-fish-tools-meta\
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -g ${gid} ${group} \
    && useradd -u "${uid}" -g "${gid}" -g sudo -m -s /bin/bash "${user}"\
    && echo "${user} ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

WORKDIR "${HOME}"

COPY setup /usr/local/bin/setup

USER ${user}
ENTRYPOINT ["setup"]
