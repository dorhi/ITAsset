@echo off
echo Downloading Spring Boot project...
curl -s -G https://start.spring.io/starter.zip -d dependencies=web,jdbc,mybatis,sqlserver,session,lombok -d type=maven-project -d bootVersion=3.2.4 -d baseDir=backend -d groupId=com.saea.itasset -d artifactId=backend -o backend.zip
echo Extracting Spring Boot project...
tar -xf backend.zip
del backend.zip

echo Generating React project...
call npx -y create-vite@latest frontend --template react
cd frontend
call npm install
call npm install react-router-dom axios html5-qrcode lucide-react
cd ..
echo Done.
