# bpm-sbh

<img src="https://ci.appveyor.com/api/projects/status/pm6m6c5nwy4nfm06?svg=true" alt="Project Build">
<img src="https://ci.appveyor.com/api/projects/status/pm6m6c5nwy4nfm06?svg=true&passingText=Test%20-%20Passed" alt="Test Status">

compile:
copy `application.yaml` to `application-dev.yaml`
edit `application-dev.yaml` with your config
mvn clean

migrate database:
mvn clean flyway:migrate -DskipTests -Dflyway.configFiles=flyway.properties

run:
mvn spring-boot:run  

test:
mvn test

setting db:
application.yml

migration sql:
on sql folder *mysql

user demo:
username: admin 
password: admin

thunder client:
src/main/resources/thunder/

CustomService and API can be add by adding class on JerseyConfig.java
