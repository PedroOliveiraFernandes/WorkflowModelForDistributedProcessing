# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use
FROM <repository-name>/activity-base-image:latest

RUN apk add python3

RUN mkdir /var/tasks
ENV TASK_EXECUTABLE_DIRECTORY="/var/tasks"

COPY  RampUpTask/target/RampUpTask.jar /var/tasks/RampUpTask.jar
COPY  AdditionTask/target/AdditionTask.jar /var/tasks/AdditionTask.jar
COPY  MultiplicationTask/src/MultiplicationTask.py /var/tasks/MultiplicationTask.py
