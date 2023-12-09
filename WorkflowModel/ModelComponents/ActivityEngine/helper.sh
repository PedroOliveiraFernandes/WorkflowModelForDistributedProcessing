# ATTENTION, you will need to change the <repository-name> for the DockerHub repository that you prefer to use

docker build -t activity-base-image .
docker tag activity-base-image <repository-name>/activity-base-image:latest
docker push <repository-name>/activity-base-image:latest
