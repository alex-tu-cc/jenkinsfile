
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "plymouth upower network-manager thermald modemmanager dkms"
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
                        docker {
                            label 'docker'
                            image "${env.DOCKER_REPO}/oem-taipei-bot"
                            args 'fish-fix help'
                        }
                    }
                    steps {
                        sh 'cat /etc/*-release'
                    }
                }
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        sh '''
                            mkdir -p ${OUTDIR}
                            eval ${RUN_DOCKER_TAIPEI_BOT} \"git clone -b test-jenkins git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools && lp-fish-tools/bin/pack-fish --base bionic-base --template ${TEMPLATE} --deb \\"${TARGET_DEB}\\" --outdir ${OUTDIR}\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./
                            rm ${OUTDIR}/${TEMPLATE}_fish1.tar.gz
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: '${env.TEMPLATE}_fish1.tar.gz'
                        }
                    }
                }
                stage('beaver-osp1') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        sh '''
                            mkdir -p ${OUTDIR}
                            eval ${RUN_DOCKER_TAIPEI_BOT} \"pack-fish --base beaver-osp1 --template ${TEMPLATE} --deb \\"${TARGET_DEB}\\" --outdir ${OUTDIR}\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./
                            rm ${OUTDIR}/${TEMPLATE}_fish1.tar.gz
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: '${env.TEMPLATE}_fish1.tar.gz'
                        }
                    }
                }
                stage('oem-taipei-bot-2') {
                    agent {
                        label 'docker'
                    }
                    steps {
                            sh '''
                                sh 'docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot \"fish-fix help\"'
                            '''
                    }
                }
            }
        }
    }
}

