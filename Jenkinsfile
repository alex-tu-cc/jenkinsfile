
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
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
                stage('docker-build-img') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        sh '''#!/bin/bash
                            set -ex
                            mkdir -p ${OUTDIR}
                            mkdir -p artifacts
                            rm -rf artifacts/*
                            eval ${RUN_DOCKER_TAIPEI_BOT} \\"ls /srv/credential/ \\&\\& source /srv/credential/set-env \\&\\& \
                                ${git_cmd} \\&\\& \
                                cd dockers-for-somerville \\&\\& \
                                bats tests/tests.bats \\&\\& \
                                bats tests/test_push.bats \\"
                        '''
                    }
                }
            }
        }
    }
}

