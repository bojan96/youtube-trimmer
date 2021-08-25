FROM adoptopenjdk/openjdk11:debian-jre

COPY maven /app
RUN ["apt-get", "update"]
RUN ["apt-get", "-y", "install", "ffmpeg"]
WORKDIR /app
CMD ["java", "-jar", "${project.artifactId}-${project.version}.jar"]
