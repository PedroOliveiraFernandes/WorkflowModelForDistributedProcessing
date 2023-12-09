# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use

docker build -t calculation-workflow-activities .
docker tag calculation-workflow-activities <repository-name>/calculation-workflow-activities:latest
docker push <repository-name>/calculation-workflow-activities:latest
