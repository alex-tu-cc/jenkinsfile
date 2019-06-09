def call(String target, String series, String update,String delete ) {
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
