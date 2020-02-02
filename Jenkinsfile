@Library('somervillJenkinsLib') _

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
                        timeout(time: 20, unit: 'SECONDS') {
                                script {
                                    // Show the select input modal
                                   def INPUT_PARAMS = input message: 'Please Provide Parameters', ok: 'Next',
                                                    parameters: [
                                                    choice(name: 'is_update_pkgs', choices: ['no','yes'].join('\n'), description: 'Do you want update secure and mesa pkgs for stagings')]

                                    env.is_update_pkgs = INPUT_PARAMS
                                    print env.is_update_pkgs
                                }
                        }
                    } catch (e) {
                        print "timeout: set default value = no"
                        env.is_update_pkgs = 'no'
                    }
                    try {
                        sh 'docker ps | grep docker-volumes'
                        sh 'rm -rf artifacts/*'
                    } catch (e) {
                        sh 'echo error!'
                    }
                }


            }
        }
        stage('parallel-clean') {
            parallel {
                stage('oem-taipei-bot-0') {
                    agent {
                       label 'docker'
                    }
                    steps {
                        sh 'cat /etc/*-release'
                    }
                }
                // use stage as image name. e.g. dell-bto-bionic-bionic-master, dell-bot-bionic-beaver-osp1 ..etc
                stage('dell-bto-bionic-bionic-master') {
                    steps {
                        // use parameter as branch name. e.g. staging, alloem ..etc
                        // so that it composed dell-bto-bionic-beaver-osp1-alloem
                        clean_manifest('staging');
                    }
                }
                stage('dell-bto-bionic-beaver-osp1') {
                    steps {
                        clean_manifest('staging');
                        clean_manifest('alloem');
                        fishManifest series:'bionic', target:'beaver-osp1-alloem', base:'beaver-osp1', update:'1861491', delete:'1852059'
                    }
                }
            }
        }

        stage('pack-fish-gfx') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        stage('pack-fish-updatepkgs') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        stage('pack-fish-unattended') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
    }
}

def clean_manifest(String b) {
    env.branch = "${b}"
    script {
        env.DOCKER_REPO=globalVar.internalDockerRepo()
        env.DOCKER_VOL=globalVar.internalDockerVolum()
        try {
        sh '''#!/bin/bash
            set -ex
            RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot \
                                    --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"

            $RUN_DOCKER_TAIPEI_BOT " \
            bzr branch lp:~oem-solutions-engineers/bugsy-config/${STAGE_NAME}-${branch} && \
            VER=\\$(bzr branch lp:~oem-solutions-engineers/bugsy-config/${STAGE_NAME}  2>&1 | grep revisions | cut -d \\" \\" -f2) && \
            echo \\$VER && \
            yes| rm -rf ${STAGE_NAME}-${branch}/* && \
            cp -rf ${STAGE_NAME}/* ${STAGE_NAME}-${branch}/ && \
            cd ${STAGE_NAME}-$branch/ && \
            bzr add . && \
            bzr commit -m \\"replaced by ${STAGE_NAME} bzr \\$VER\\" || true && bzr log | head && \
            bzr push :parent || echo "skip ${STAGE_NAME}-${branch}" \
            "
        '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}

