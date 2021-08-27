FROM adoptopenjdk/openjdk11:debianslim-jre

COPY maven /app
RUN mkdir /app/workdir
RUN apt-get update
RUN apt-get -y install ffmpeg
WORKDIR /app
CMD ["java", "-jar", "-Dspring.profiles.active=k8s", "${project.artifactId}-${project.version}.jar"]
