pipeline {
    agent none
    stages {
        stage('pack fish') {
            agent {
                docker {
                    label 'docker'
                    image 'oem-taipei-bot'
                }
            }
            parallel bionic-base: {
                steps {
                    git clone -b pack-fish git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools
                    /lp-fish-tools/bin/pack-fish.sh --base bionic-base --deb tmux
                }
            },
            beaver-osp1: {
                steps {
                    git clone -b pack-fish git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools
                    /lp-fish-tools/bin/pack-fish.sh --base bionic-base --template upgrade
                }
            }
        }
    }
}
