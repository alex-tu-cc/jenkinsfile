
pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('prepare') {
            agent {
                label 'docker'
            }
            steps {
                script {
                    try {
                        sh 'docker ps | grep docker-volumes'
                        sh 'rm -rf artifacts/*'
                    } catch (e) {
                        sh 'echo error!'
                    }
                }
            }
        }
        stage('parallel') {
            parallel {
                stage('oem-taipei-bot-0') {
                    agent {
                       label 'docker'
                    }
                    steps {
                        sh 'cat /etc/*-release'
                    }
                }
                stage('bionic-master') {
                    steps {
                        clean_manifest();
                    }
                }
                stage('beaver-osp1') {
                    steps {
                        clean_manifest();
                    }
                }
            }
        }
    }
}

def clean_manifest() {
    script {
        try {
        sh '''#!/bin/bash
            set -ex
            DOCKER_REPO="somerville-jenkins.cctu.space:5000"
            RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot"

            $RUN_DOCKER_TAIPEI_BOT " \
            bzr branch lp:~oem-solutions-engineers/bugsy-config/dell-bto-bionic-${STAGE_NAME}-staging && \
            VER=\\$(bzr branch lp:~oem-solutions-engineers/bugsy-config/dell-bto-bionic-${STAGE_NAME}  2>&1 | grep revisions | cut -d \\" \\" -f2) && \
            echo \\$VER && \
            yes| rm -rf dell-bto-bionic-${STAGE_NAME}-staging/* && \
            cp -rf dell-bto-bionic-${STAGE_NAME}/* dell-bto-bionic-${STAGE_NAME}-staging/ && \
            cd dell-bto-bionic-${STAGE_NAME}-staging/ && \
            bzr add . && \
            bzr commit -m \\"replaced by ${STAGE_NAME} bzr \\$VER\\" && bzr log | head && \\
            bzr push :parent || echo "skip clean ${STAGE_NAME}-staging" && true\
            "
        '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}
