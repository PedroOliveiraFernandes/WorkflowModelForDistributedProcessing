# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use

docker build -t long-running-workflow-activity .
docker tag long-running-workflow-activity <repository-name>/long-running-workflow-activity:latest
docker push <repository-name>/long-running-workflow-activity:latest
