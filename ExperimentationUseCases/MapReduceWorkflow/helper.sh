# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use

docker build -t map-reduce-activities .
docker tag map-reduce-activities <repository-name>/map-reduce-activities:latest
docker push <repository-name>/map-reduce-activities:latest
