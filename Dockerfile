FROM openjdk:21
COPY build/libs/musicion-*.*.*.war musicion-server.war
ENTRYPOINT ["java","-jar","/musicion-server.war"]
