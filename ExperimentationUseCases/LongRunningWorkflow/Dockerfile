# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use
FROM <repository-name>/activity-base-image:latest

RUN mkdir /var/task
ENV TASK_EXECUTABLE_DIRECTORY="/var/task"

COPY  target/LongRunningTask.jar /var/task/LongRunningTask.jar
