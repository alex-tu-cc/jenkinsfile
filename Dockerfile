# command to build image then push it
#$ BASE=beaver-osp1;eval sed -i \'1s/^/FROM somerville-jenkins.cctu.space:5000\\/$BASE\\n/\' Dockerfile;docker build -t somerville-jenkins.cctu.space:5000/$BASE.collect-deps .;docker push somerville-jenkins.cctu.space:5000/$BASE.collect-deps
#FROM somerville-jenkins.cctu.space:5000/${BASE}
LABEL MAINTAINER="Alex Tu <alextu@cctu.space>"

ENV DEBIAN_FRONTEND noninteractive
RUN rm /etc/kernel/postinst.d/zz-update-grub

RUN apt-get update \
    && apt-get install --no-install-recommends -y pxz \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean
#
#RUN groupadd -g ${gid} ${group} \
#    && useradd -u "${uid}" -g "${gid}" -g sudo -m -s /bin/bash "${user}"\
#    && echo "${user} ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

#RUN sed -i /etc/ssh/sshd_config \
#        -e 's/#Port.*/Port 2222/' \
#        -e 's/#PermitRootLogin.*/PermitRootLogin no/' \
#        -e 's/#RSAAuthentication.*/RSAAuthentication yes/'  \
#        -e 's/#PasswordAuthentication.*/PasswordAuthentication no/' \
#        -e 's/#SyslogFacility.*/SyslogFacility AUTH/' \
#        -e 's/#LogLevel.*/LogLevel INFO/' && \
#    mkdir /var/run/sshd

#VOLUME "${JENKINS_AGENT_HOME}"
#WORKDIR "${HOME}"

COPY collect-deps /usr/local/bin/

#USER ${user}
ENTRYPOINT ["collect-deps"]
