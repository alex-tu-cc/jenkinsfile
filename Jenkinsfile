pipeline {
    agent docker
    stages {
/*
        stage('setup') {
            agent {
                docker {
                    label 'docker'
                    image 'maven:3-alpine'
                }
            }
            steps {
                sh 'cat /etc/*-release'
            }
        }
*/
        stage('pack fish') {
        ¦   agent {
    ¦   ¦   ¦   docker {
    ¦   ¦   ¦   ¦   label 'docker'
    ¦   ¦   ¦   ¦   image 'oem-taipei-bot'
    ¦   ¦   ¦   }
    ¦   ¦   }
            parallel bionic-base:{
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
/*
        stage('archlinux') {
            agent {
                docker {
                    label 'docker'
                    image 'base/archlinux'
                }
            }
            steps {
                sh 'cat /etc/*-release'
            }
        }
*/
    }
}
