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
                        print "timeout: set default value = yes"
                        env.is_update_pkgs = 'yes'
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
        // We don't need the private built nvidia-prime and ubuntu-drivers-common for now
        //stage('pack-fish-nvidia-rtd3') {
        //    when { environment name: 'is_update_pkgs', value: 'yes' }
        //    steps { script {
        //        try {
        //              build("${STAGE_NAME}")
        //        } catch(e) {
        //            unstable ("${STAGE_NAME} failed but continue.")
        //        }
        //    } }
        //}
        //stage('pack-fish-ubuntu-desktop-xstaging') {
        //    when { environment name: 'is_update_pkgs', value: 'yes' }
        //    steps { script {
        //        try {
        //              build("${STAGE_NAME}")
        //        } catch(e) {
        //            unstable ("${STAGE_NAME} failed but continue.")
        //        }
        //    } }
        //}
        //stage('pack-fish-ubuntu-desktop') {
        //    when { environment name: 'is_update_pkgs', value: 'yes' }
        //    steps { script {
        //        try {
        //              build("${STAGE_NAME}")
        //        } catch(e) {
        //            unstable ("${STAGE_NAME} failed but continue.")
        //        }
        //    } }
        //}
        stage('pack-fish-openssh') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        //stage('pack-fish-mainline') {
        //    when { environment name: 'is_update_pkgs', value: 'yes' }
        //    steps { script {
        //        try {
        //              build("${STAGE_NAME}")
        //        } catch(e) {
        //            unstable ("${STAGE_NAME} failed but continue.")
        //        }
        //    } }
        //}
        //stage('pack-fish-drm-tip') {
        //    when { environment name: 'is_update_pkgs', value: 'yes' }
        //    steps { script {
        //        try {
        //              build("${STAGE_NAME}")
        //        } catch(e) {
        //            unstable ("${STAGE_NAME} failed but continue.")
        //        }
        //    } }
        //}
         // use stage as image name. e.g. dell-bto-bionic-bionic-master, dell-bot-bionic-beaver-osp1 ..etc
         stage('dell-bto-focal-fossa-next') {
             steps {
                 clean_manifest('edge-staging');
                 fishManifest series:'focal',tag:'fossa-edge-staging' ,target:'fossa-edge-staging', base:'fossa-next',delete:'1933063 1931212 1909530'
             }
         }
         // use stage as image name. e.g. dell-bto-bionic-bionic-master, dell-bot-bionic-beaver-osp1 ..etc
         stage('dell-bto-focal-fossa') {
             steps {
                 clean_manifest('staging');
                 fishManifest series:'focal',tag:'fossa-staging' ,target:'fossa-staging', base:'fossa',delete:'1931969'
             }
         }
        stage('snapshot-checkbox-dev-testing') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        stage('pack-fish-checkbox-pkgs') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        stage('pack-fish-maas-override') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                      build("${STAGE_NAME}")
                } catch(e) {
                    unstable ("${STAGE_NAME} failed but continue.")
                }
            } }
        }
        stage('trigger-outside-server') {
            when { environment name: 'is_update_pkgs', value: 'yes' }
            steps { script {
                try {
                // TODO: polling the status till the Jenkins done the build.
                // 1. check "nextBuildNumber" from {jenkins-job}/api/json before triggering remote server.
                      build("${STAGE_NAME}")
                // 2. check "lastBuild" and it's "building" and "result" by {jenkins-job}/{build number}/api/json
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
            if [ -z "${STAGE_NAME##*bionic*}" ]; then
                RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot \
                                        --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot:bionic"
    
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
            elif [ -z "${STAGE_NAME##*fossa-next*}" ]; then
                echo "[DEBUG] Handling branches for fossa-next"
                # special case that fossa-edge/fossa-edge-staging is tracking fossa-next.
                # so we don't want to have another  fossa-next-edge-staging.
                if [ "$branch" = "edge-staging" ]; then
                    echo "[DEBUG] specially handle fossa-edge-staging"
                    RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot \
                                            --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"
                    $RUN_DOCKER_TAIPEI_BOT " \
                    git clone lp:~oem-solutions-engineers/bugsy-config/+git/somerville-project-manifests -b ${STAGE_NAME} --depth=1 && \
                    cd somerville-project-manifests && \
                    git rev-parse --short HEAD && \
                    git push -f origin origin/${STAGE_NAME}:dell-bto-focal-fossa-edge-staging \
                    "
                else
                    RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot \
                                            --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"
    
                    $RUN_DOCKER_TAIPEI_BOT " \
                    git clone lp:~oem-solutions-engineers/bugsy-config/+git/somerville-project-manifests -b ${STAGE_NAME} --depth=1 && \
                    cd somerville-project-manifests && \
                    git rev-parse --short HEAD && \
                    git push -f origin origin/${STAGE_NAME}:${STAGE_NAME}-${branch} \
                    "
                fi
            else
                RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot \
                                        --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"
    
                $RUN_DOCKER_TAIPEI_BOT " \
                git clone lp:~oem-solutions-engineers/bugsy-config/+git/somerville-project-manifests -b ${STAGE_NAME} --depth=1 && \
                cd somerville-project-manifests && \
                git rev-parse --short HEAD && \
                git push -f origin origin/${STAGE_NAME}:${STAGE_NAME}-${branch} \
                "
            fi
        '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}

