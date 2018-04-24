FROM openjdk:9-jre
EXPOSE 80/tcp
COPY build/libs/sample-azure-k8n.jar sample-azure-k8n.jar
ENTRYPOINT ["java" "-jar", "/sample-azure-k8n.jar"]