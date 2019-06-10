pipeline {
    agent none
    stages {
        stage('pack fish in parallel') {
            parallel {
                stage ('based on bionic-base') {
                    agent {
                        docker {
                            label 'docker'
                            image 'somerville-jenkins.cctu.space:5000/oem-taipei-bot'
                        }
                    }
                    steps {
                        sh 'lsb_release -a'
                        //sh 'git clone -b pack-fish git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools'
                        //sh '/lp-fish-tools/bin/pack-fish.sh --base bionic-base --deb tmux'
                    }
                }
                stage ('based on beaver-osp1') {
                    agent {
                        docker {
                            label 'docker'
                            image 'somerville-jenkins.cctu.space:5000/oem-taipei-bot'
                        }
                    }
                    steps {
                        //sh 'git clone -b pack-fish git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools'
                        //sh '/lp-fish-tools/bin/pack-fish.sh --base bionic-base --deb tmux'
                    }
                }
            }
        }
    }
}
