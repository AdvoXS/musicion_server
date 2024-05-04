FROM openjdk:21
COPY build/libs/musicion-0.0.1.war musicion-0.0.1.war
ENTRYPOINT ["java","-jar","/musicion-0.0.1.war"]
