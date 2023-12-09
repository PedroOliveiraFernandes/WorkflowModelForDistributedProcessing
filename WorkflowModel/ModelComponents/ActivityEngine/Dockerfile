FROM alpine:3.17
RUN apk add openjdk11
COPY  target/ActivityEngine.jar /var/activity/ActivityEngine.jar

ENV PATH_TO_WORK_DIRECTORY="/var/workingDirectory"
ENV PATH_TO_TASK_RESULTS_FILE="/var/results/taskResults.json"
ENTRYPOINT ["java", "-jar" , "/var/activity/ActivityEngine.jar"]
