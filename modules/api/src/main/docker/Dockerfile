FROM adoptopenjdk/openjdk11:alpine-jre

COPY maven /app
WORKDIR /app
CMD ["java", "-jar", "${project.artifactId}-${project.version}.jar"]