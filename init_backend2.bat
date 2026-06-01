@echo off
curl -s -G https://start.spring.io/starter.zip -d dependencies=web,jdbc,mybatis,sqlserver,session,lombok -d type=maven-project -d baseDir=backend -d groupId=com.saea.itasset -d artifactId=backend -o backend.zip
tar -xf backend.zip
del backend.zip
