pipeline {
    agent any

    environment {
        // ⚠️ 운영 서버의 실제 IP와 SSH 접속 계정명으로 변경해 주세요.
        TARGET_SERVER = '10.40.1.83' 
        TARGET_USER   = 'udocker'        
        DEPLOY_PATH   = '/opt/itasset'
    }

    stages {
        stage('1. Git Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Transfer Files to Server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'operating-server-ssh', keyFileVariable: 'SSH_KEY')]) {
                    // 1. 운영 서버 배포 디렉토리 생성
                    sh "ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${TARGET_USER}@${TARGET_SERVER} 'mkdir -p ${DEPLOY_PATH}'"
                    
                    // 2. 빌드된 파일과 소스코드를 운영 서버로 동기화 (.env 및 불필요 파일 제외)
                    sh "rsync -avz --delete --exclude='.git' --exclude='node_modules' --exclude='.env' -e \"ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no\" ./ ${TARGET_USER}@${TARGET_SERVER}:${DEPLOY_PATH}/"
                }
            }
        }

        stage('3. Docker Container Build & RUN') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'operating-server-ssh', keyFileVariable: 'SSH_KEY')]) {
                    // 3. 운영 서버에서 Docker Compose를 빌드하여 백그라운드 구동 및 오래된 미사용 이미지 정리
                    sh """
                    ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${TARGET_USER}@${TARGET_SERVER} '
                        cd ${DEPLOY_PATH} &&
                        docker compose up -d --build &&
                        docker image prune -f
                    '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '🎉 IT 자산 관리 서비스가 성공적으로 도커라이징되어 자동 배포되었습니다!'
        }
        failure {
            echo '❌ CI/CD 배포 과정 중 오류가 발생했습니다. Jenkins 빌드 콘솔을 확인하세요.'
        }
    }
}