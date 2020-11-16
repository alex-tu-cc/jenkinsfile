//def call(String project = 'somerville', String series, String target, String base, String update,String delete ) {
def call(Map config) {
    node {
        env.base = "${config.base}"
        env.project = "somerville"
        env.target = "${config.target}"
        env.series = "${config.series}"
        env.update = "${config.update}"
        env.delete = "${config.delete}"
        script {
            env.DOCKER_REPO=globalVar.internalDockerRepo()
            env.DOCKER_VOL=globalVar.internalDockerVolum()
            try {
            sh '''#!/bin/bash
                set -ex
                RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"
                if [ "${series}" == "bionic" ]; then
                    command="fish-manifest -b -p ${project} -r ${series} -e -c --target ${target} ${base} --postRTS "
                else
                    command="fish-manifest --git -b -p ${project} -r ${series} -e -c --target ${target} ${base} --postRTS "
                fi
                [ -n "${update}" ] && [ "${update}" != "null" ] && command="$command -u ${update}"
                [ -n "${delete}" ] && [ "${delete}" != "null" ] && command="$command --delete ${delete}"
                $RUN_DOCKER_TAIPEI_BOT "$command"
            '''
            } catch (e) {
                error("exception:" + e)
            }
        }
    }
}
