FROM openjdk:9-jre
COPY build/libs/sample-azure-k8n.jar sample-azure-k8n.jar
ENTRYPOINT ["java", "-jar", "/sample-azure-k8n.jar"]