# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use
FROM <repository-name>/activity-base-image:latest

#install letter font in order to annotate the images
RUN apk --no-cache add msttcorefonts-installer fontconfig \
    && update-ms-fonts \
    && fc-cache -f

RUN mkdir /var/task
ENV TASK_EXECUTABLE_DIRECTORY="/var/tasks"
ENV GOOGLE_APPLICATION_CREDENTIALS="/var/workingDirectory/gcpCredentials/gcp_key.json"

COPY  ImageUploaderTask/target/ImageUploaderTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/ImageUploaderTask.jar
COPY  TextDetectorTask/target/TextDetectorTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/TextDetectorTask.jar
COPY  ObjectDetectorTask/target/ObjectDetectorTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/ObjectDetectorTask.jar
COPY  TextTranslationTask/target/TextTranslationTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/TextTranslationTask.jar
COPY  ImageAnnotatorTask/target/ImageAnnotatorTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/ImageAnnotatorTask.jar
COPY  ImageDownloaderTask/target/ImageDownloaderTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/ImageDownloaderTask.jar
