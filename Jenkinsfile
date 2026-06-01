pipeline {
    agent any
    tools {
        nodejs 'NodeJS 20' // Jenkins에 등록한 NodeJS 도구 호출
    }
    environment {
        TARGET_SERVER = '10.40.0.193' // 👈 실제 가상 머신(운영 서버) IP 입력
        TARGET_USER   = 'saea'
        DEPLOY_PATH   = '/opt/itasset'
    }
    stages {
        stage('1. Checkout') {
            steps {
                checkout scm
            }
        }
        stage('2. Backend 빌드') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
        stage('3. 소스코드 및 JAR 전송') {
            steps {
                sshagent(['operating-server-ssh']) {
                    // 운영 서버에 배포 대상 디렉토리 준비
                    sh "ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${TARGET_SERVER} 'mkdir -p ${DEPLOY_PATH}'"
                    
                    // .env 파일과 SSL 인증서를 훼손하지 않기 위해 제외 처리하고 소스 전송
                    sh "rsync -avz --exclude='.git' --exclude='node_modules' --exclude='.env' -e 'ssh -o StrictHostKeyChecking=no' ./ ${TARGET_USER}@${TARGET_SERVER}:${DEPLOY_PATH}/"
                }
            }
        }
        stage('4. Docker 컨테이너 무중단 빌드 및 구동') {
            steps {
                sshagent(['operating-server-ssh']) {
                    // 운영 서버에 직접 원격 명령을 내려 컨테이너 빌드 및 백그라운드 재부팅을 실현
                    sh """
                    ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${TARGET_SERVER} '
                        cd ${DEPLOY_PATH} &&
                        docker compose pull &&
                        docker compose up --build -d &&
                        docker image prune -f
                    '
                    """
                }
            }
        }
    }
    post {
        success {
            echo '🎉 IT 자산 관리 서비스가 성공적으로 도커라이징되어 무중단 배포되었습니다!'
        }
        failure {
            echo '❌ 파이프라인 실행 중 에러가 발생했습니다. Jenkins 빌드 콘솔 창을 참고하세요.'
        }
    }
}