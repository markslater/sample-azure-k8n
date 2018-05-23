FROM openjdk:9-jre
# TODO What's this for?  Nothing's listening on port 80
EXPOSE 80/tcp
COPY build/libs/sample-azure-k8n.jar sample-azure-k8n.jar
ENTRYPOINT ["java", "-jar", "/sample-azure-k8n.jar"]