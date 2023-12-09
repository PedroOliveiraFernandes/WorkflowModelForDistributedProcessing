# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use
FROM <repository-name>/activity-base-image:latest

RUN mkdir /var/tasks
ENV TASK_EXECUTABLE_DIRECTORY="/var/tasks"

COPY  SplitTask/target/SplitTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/SplitTask.jar
COPY  MapTask/target/MapTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/MapTask.jar
COPY  MergeSortSplitTask/target/MergeSortSplitTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/MergeSortSplitTask.jar
COPY  ReduceTask/target/ReduceTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/ReduceTask.jar
COPY  FinalResultTask/target/FinalResultTask-1.0-SNAPSHOT-jar-with-dependencies.jar /var/tasks/FinalResultTask.jar
