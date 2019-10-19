pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "plymouth upower network-manager thermald modemmanager dkms"
        LP_NUM = "1838518"
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
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        script {
                            try {
                                copyArtifacts(
                                projectName: 'pack-fish-updatepkgs-test',
                                filter: "artifacts/*.dell",
                                target: 'latest_build',
                                selector: lastSuccessful());
                            } catch(e) {
                                echo "No lastSuccessful build, let treat this build as 1st build"
                            }
                            try {
                                status = sh(returnStatus: true,
                                    script: '''#!/bin/bash
                                    set -xe
                                    mkdir -p ${OUTDIR}
                                    mkdir -p artifacts
                                    rm -rf artifacts/*
                                    eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base ${STAGE_NAME} --template ${TEMPLATE} --deb ${TARGET_DEB} --outdir ${OUTDIR}\\"
                                    cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                                    tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                                    mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                                    [ \"$(find artifacts latest_build -name ${GIT_BRANCH##origin/}-${STAGE_NAME}-*dell | xargs md5sum |cut -d ' ' -f1 | uniq | wc -l)\" == "1" ] && ret=2
                                    rm -rf ${OUTDIR} latest_build
                                    exit $ret
                                    ''')
                                echo "status = " + status
                                if ( status == 1 ) {throw new Exception("packing fish failed somewhere!")}
                                if ( status == 2 ) {unstable('no new package need to be updated'); echo "set UNSTABLE"}
                            } catch(e) {
                                echo "exception = " + e
                            }
                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
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
                        script {
                            try {
                                copyArtifacts(
                                projectName: 'pack-fish-updatepkgs-test',
                                filter: "artifacts/*.dell",
                                target: 'latest_build',
                                selector: lastSuccessful());
                            } catch(e) {
                                echo "No lastSuccessful build, let treat this build as 1st build"
                            }
                            try {
                                status = sh(returnStatus: true,
                                    script: '''#!/bin/bash
                                    set -xe
                                    mkdir -p ${OUTDIR}
                                    mkdir -p artifacts
                                    rm -rf artifacts/*
                                    eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base beaver-osp1 --template ${TEMPLATE} --deb ${TARGET_DEB} --outdir ${OUTDIR}\\"
                                    cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                                    tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                                    mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                                    [ \"$(find artifacts latest_build -name ${GIT_BRANCH##origin/}-${STAGE_NAME}-*dell | xargs md5sum |cut -d ' ' -f1 | uniq | wc -l)\" == "1" ] && ret=2
                                    rm -rf ${OUTDIR} latest_build
                                    exit $ret
                                    ''')
                                echo "status = " + status
                                if ( status == 1 ) {throw new Exception("packing fish failed somewhere!")}
                                if ( status == 2 ) {unstable('no new package need to be updated'); echo "set UNSTABLE"}
                            } catch(e) {
                                echo "exception = " + e
                            }
                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                        }
                    }
                }
            }
        }

        stage('fish-fix') {
            agent {
                label 'docker'
            }
            steps {
               script {
                   try {
                       copyArtifacts(
                       projectName: "${JOB_NAME}",
                       filter: "artifacts/*.tar.gz",
                       target: 'latest_build',
                       selector: specific("${BUILD_NUMBER}"));
                   } catch(e) {
                       error("No lastSuccessful build, we should be be here!")
                   }
                   try {
                       sh '''#!/bin/bash
                            set -ex
                            find latest_build
                            fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep bionic-base)"
                            echo fish-fix $fish_tarball
                            docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                            docker cp $fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                            target_fish=$(basename $fish_tarball)
                            docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "ls"
                            docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes| fish-fix --nodep -b -f $target_fish -c misc $LP_NUM"
                            docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                            docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                            echo "=========================================================="
                            echo "${STAGE_NAME} fished"
                            echo "=========================================================="
                       '''
                   } catch (e) {
                       error("exception:" + e)
                   }
                }
            }
        }

        stage('fish-manifest') {
            agent {
                label 'docker'
            }
            steps {
               script {
                   try {
                       sh '''#!/bin/bash
                            set -ex
                            find latest_build
                            fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep bionic-base)"
                            echo fish-fix $fish_tarball
                            docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                            docker cp $fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                            docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target bionic-master-staging  bionic-master --postRTS -u $LP_NUM"
                            docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target beaver-osp1-staging  beaver-osp1 --postRTS -u $LP_NUM"
                            docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                            docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                       '''
                   } catch (e) {
                       error("exception:" + e)
                   }
                }
            }
        }

    }
}

