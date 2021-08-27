FROM adoptopenjdk/openjdk11:alpine-jre

COPY maven /app
WORKDIR /app
CMD ["java", "-jar", "-Dspring.profiles.active=k8s", "${project.artifactId}-${project.version}.jar"]