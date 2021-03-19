
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker pull \${DOCKER_REPO}/oem-taipei-bot && docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        LP_FOSSA="1912397"
    }
    stages {
        stage('prepare') {
            agent {
                label 'docker'
            }
            steps {
                script {
                    deleteDir()
                }
            }
        }
        stage('parallel') {
            parallel {
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        sh "echo testing, ignore me"
                    }
                }
                stage('fossa') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="openssh"
                    }
                    steps {
                        pack_fish();
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                            echo "[${STAGE_NAME}] success and pushing artifacts"
                        }
                    }
                }
            }
        }
        stage('fish-fix-manifest') {
            agent {
                label 'docker'
            }
            steps {
                fish_fix_manifest();
            }
        }

    }
}

def pack_fish() {
    script {
        try {
            copyArtifacts(
            projectName: "${JOB_NAME}",
            filter: "artifacts/*.dell",
            target: 'latest_build',
            selector: lastSuccessful());
        } catch(e) {
            echo "No lastSuccessful build, let treat this build as 1st build"
        }

        try {
            status = sh(returnStatus: true,
                script: '''#!/bin/bash
                set -ex
                mkdir -p ${OUTDIR}
                mkdir -p artifacts
                rm -rf  artifacts/*
                eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base ${STAGE_NAME} --template ${TEMPLATE} --outdir ${OUTDIR}\\"
                cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`${BUILD_NUMBER}_fish1.tar.gz
                tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`${BUILD_NUMBER}_fish1.tar.gz.prepackage.dell
                [ -d latest_build ] && [ \"$(find artifacts latest_build -name ${GIT_BRANCH##origin/}-${STAGE_NAME}-*dell | xargs md5sum |cut -d ' ' -f1 | uniq | wc -l)\" == "1" ] && ret=2
                rm -rf ${OUTDIR} latest_build
                exit $ret
            ''')
            echo "status = " + status
            if ( status == 1 ) {throw new Exception("packing fish failed somewhere!")}
            if ( status == 2 ) {unstable('no new package need to be updated'); echo "set UNSTABLE"}
        } catch(e) {
            echo "exception = " + e
            currentBuild.result = 'FAILURE'
        }
    }
}
def fish_fix_manifest() {
    env.new_pkgs="true"
    script {
        try {
            copyArtifacts(
            projectName: "${JOB_NAME}",
            filter: "artifacts/*.tar.gz",
            target: 'latest_build',
            selector: specific("${BUILD_NUMBER}"));
        } catch(e) {
            echo "Not a successful build, so only fish-manifest to stagings"
            env.new_pkgs="false"
        }
        try {
            sh '''#!/bin/bash
                set -ex
                docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                # a workaround to wait credential is ready and FishInitFile is there
                while [ -z "$(docker exec -t oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} ls | grep FishInitFile)" ]; do
                    sleep 10
                done

                if [ "${new_pkgs}" == "true" ]; then
                    find latest_build
                    fossa_fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep fossa)"
                    echo fish-fix $fish_tarball
                    docker cp $fossa_fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                    fossa_target_fish=$(basename $fossa_fish_tarball)
                    # host tarball on lp ticket
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "ls"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes| fish-fix --nodep -b -f $fossa_target_fish -c misc $LP_FOSSA"
                    # host it on git repository
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "git clone git+ssh://git.launchpad.net/~oem-solutions-engineers/pc-enablement/+git/pack-fish.openssh-fossa"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "rm -rf pack-fish.openssh-fossa/*"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "tar -C pack-fish.openssh-fossa -xvf /home/oem-taipei-bot/$fossa_target_fish"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "git -C pack-fish.openssh-fossa add . || true"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "git -C pack-fish.openssh-fossa commit -m "update from $fossa_target_fish" || true"
                fi

                ## land the fish to nvidia staging manifest
                #docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes|fish-manifest --git -b -p somerville -r focal -e -c --target fossa-nvstaging fossa --postRTS -u $LP_FOSSA"

                ## land the fish to rkl staging manifest
                #docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes|fish-manifest --git -b -p somerville -r focal -e -c --target fossa-rklstaging fossa --postRTS -u $LP_FOSSA"

                docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
            '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}
