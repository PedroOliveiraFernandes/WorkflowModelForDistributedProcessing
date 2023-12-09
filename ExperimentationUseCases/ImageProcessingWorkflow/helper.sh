# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use

docker build -t image-processing-workflow-activities .
docker tag image-processing-workflow-activities <repository-name>/image-processing-workflow-activities:latest
docker push <repository-name>/image-processing-workflow-activities:latest
