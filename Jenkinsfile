
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
                        fish_manifest('beaver-osp1-alloem', 'beaver-osp1', '1861491', '1852059');
                    }
                }
            }
        }
    }
}

def clean_manifest(String b) {
    env.branch = "${b}"
    script {
        try {
        sh '''#!/bin/bash
            set -ex
            DOCKER_REPO="somerville-jenkins.cctu.space:5000"
            RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot"

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

def fish_manifest(String target, String series, String update,String delete ) {
    env.target = "${target}"
    env.series = "${series}"
    env.update = "${update}"
    env.delete = "${delete}"
    script {
        try {
        sh '''#!/bin/bash
            set -ex
            DOCKER_REPO="somerville-jenkins.cctu.space:5000"
            RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot"
            command="fish-manifest -p somerville -r bionic -e -c --target ${target} ${series} --postRTS "
            [ -n "${update}" ] && command="$command -u ${update}"
            [ -n "${delete}" ] && command="$command --delete ${delete}"
            echo $RUN_DOCKER_TAIPEI_BOT $command
        '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}
