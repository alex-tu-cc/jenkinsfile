
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
                stage('beaver-osp1') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="upgrade"
                    }
                    steps {
                        sh '''#!/bin/bash
                            set -e
                            mkdir -p ${OUTDIR}
                            mkdir -p artifacts
                            rm -rf  artifacts/*
                            eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base beaver-osp1 --template ${TEMPLATE} --outdir ${OUTDIR}\\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                            tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                            mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                            rm -rf ${OUTDIR}
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                        }
                    }
                }
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="upgrade"
                    }
                    steps {
                        sh '''#!/bin/bash
                            set -e
                            mkdir -p ${OUTDIR}
                            mkdir -p artifacts
                            rm -rf  artifacts/*
                            eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base bionic-base --template ${TEMPLATE} --outdir ${OUTDIR}\\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                            tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                            mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                            rm -rf ${OUTDIR}
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                        }
                    }
                }
            }
        }
    }
}

