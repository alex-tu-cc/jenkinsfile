import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException

pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
    }
    stages {
        stage('ubuntu') {
            agent {
                docker {
                    label 'docker'
                    image 'ubuntu:18.04'
                }
            }
            steps {
                sh 'cat /etc/*-release'
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
                stage('oem-taipei-bot-1') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        script {
                            try {
                                sh 'env'
                                sh 'docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot \"fish-fix help\"'
                            } catch (FlowInterruptedException interruptEx) {
                                sh 'docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}'
                            }
                        }
                    }
                }
//                stage('oem-taipei-bot-upgrade') {
//                    agent {
//                        label 'docker'
//                    }
//                    steps {
//                        sh 'docker run --rm -h oem-taipei-bot -v $PWD:/srv/tmp --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot \"pack-fish.sh --base bionic-base --template upgrade --outdir /srv/tmp\"'
//                    }
//                    post {
//                        success {
//                            archiveArtifacts artifacts: 'upgrade.tar.gz'
//                        }
//                    }
//                }
                stage('oem-taipei-bot-nvidia') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        script {
                            try {
                                sh 'id'
                                sh 'mkdir -p /srv/tmp/${JOB_NAME}'
                                sh 'docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes -v /srv/tmp/${JOB_NAME}:/srv/tmp ${DOCKER_REPO}/oem-taipei-bot \"git clone -b test-jenkins git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools && lp-fish-tools/bin/pack-fish.sh --base bionic-base --template nvidia --outdir /srv/tmp\"'
                                sh 'cp /srv/tmp/${JOB_NAME}/nvidia_fish1.tar.gz ./'
                            } catch (FlowInterruptedException interruptEx) {
                                sh 'docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}'
                            }
                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'nvidia_fish1.tar.gz'
                        }
                    }
                }
            }
        }
    }
}
