$url = "https://start.spring.io/starter.zip?dependencies=web,jdbc,mybatis,sqlserver,session,lombok&type=maven-project&bootVersion=3.2.4&baseDir=backend&groupId=com.saea.itasset&artifactId=backend"
Invoke-WebRequest -Uri $url -OutFile "backend.zip"
Expand-Archive -Path "backend.zip" -DestinationPath "." -Force
Remove-Item "backend.zip"
