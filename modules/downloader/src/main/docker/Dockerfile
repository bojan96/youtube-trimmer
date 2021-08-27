FROM adoptopenjdk/openjdk11:debianslim-jre

COPY maven /app
RUN mkdir /app/workdir
RUN apt-get update
RUN apt-get -y install python
RUN curl -L https://yt-dl.org/downloads/latest/youtube-dl -o /usr/local/bin/youtube-dl
RUN chmod a+rx /usr/local/bin/youtube-dl
WORKDIR /app
CMD ["java", "-jar", "-Dspring.profiles.active=k8s", "${project.artifactId}-${project.version}.jar"]
