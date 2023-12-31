# Infrastructure
This readme file contains all the steps required to configure the infrastructure used by the workflow model prototype. This configuration is just one of many that can be used for concretization of the infrastructure.

This configuration will assume the usage of the following technologies:
- Google Cloud Platform (GCP);
- Docker;
- Java 11 (OpenJDK);
- GlusterFS;
- Ubuntu;
- RabbitMQ.

## General configuration


Each computing node of the infrastructure will be a GCP Virtual Machine (VM) of the type e2-standard-4 (4 vCPU and 16GB of memory) with the Ubuntu 20.04LTS Operative system. The development of the prototype didn't take into account security aspects of data integrity or data encryption, so to allow for an unproblematic GlusterFS installation and allow receiving HTTP requests into the VM, we assume the VM is also configured with a Firewall rule that allows all types of traffic.

We will assume the usage of 3 VM for the execution of the  Activity Bucket as well as the Activities, and a 4th unique VM just for the execution of the RabbitMQ broker.

All of the VM will require the installation of the Docker running environment which can be done following Docker's documentation below:
- https://docs.docker.com/engine/install/ubuntu/ (Docker installation)
- https://docs.docker.com/engine/install/linux-postinstall/ (post-install permissions configuration)

## VM used for the execution of the Broker
For the execution of the broker, a simple docker command is required for the execution of a container with the official RabbitMQ image (https://hub.docker.com/_/rabbitmq). 
```sh
docker run --rm --name rabbidmq -d  -p 15672:15672 -p 5672:5672 rabbitmq:3.11.3-management
```
This will start a RabbitMQ container listening on the default port of 5672.

A management dashboard with a GUI will be exposed on port 15672 with the default credentials of:
- user=guest;
- password=guest.

No further configuration/installation is required in this VM.

## VM used for the execution of the ActivityBucket and Activities
For the remaining 3 VM, run the following commands in the terminal, for the remaining configuration:

### 1. Java 11 (OpenJDK) installation
To install the Java execution environment, run the following commands:

```sh
sudo apt-get update
sudo apt-get install openjdk-11-jdk
```

### 2. GlusterFS installation and configuration
To install GlusterFS, run the following commands:

```sh
sudo add−apt−repository ppa:gluster/glusterfs-7
sudo apt update
sudo apt install glusterfs−server
sudo service glusterd start
```

For each machine, is required to update the file /etc/hosts, with the associations of the hostnames and the GCP internal IP of all the machines. 

Let's suppose that after the VM creation, these were the pairs (hostname, IP) for the machines: (VM1, 10.128.0.1); (VM2, 10.128.0.2); (VM3, 10.128.0.3). These would be the values to use in the /etc/hosts file as such.
```
127.0.0.1 localhost
VM1 10.128.0.1
VM2 10.128.0.2
VM3 10.128.0.3
```


With the associations of the hostnames and the GCP internal IP of all the machines configured, in one of the machines (for example the VM1), run the following commands:

```sh 
sudo gluster peer probe VM2
sudo gluster peer probe VM3
``` 


To support the creation of a Gluster volume run the following command in every VM:
```sh
sudo mkdir -p /data/brick/gv0
``` 

In order to create a GlusterFS volume with the name gv0, run the following commands:
```sh
sudo gluster volume create gv0 replica 3 VM1:/data/brick/gv0 VM2:/data/brick/gv0 VM3:/data/brick/gv0 force
sudo gluster volume start gv0
``` 

To configure a directory as an interaction entry point to the GlusterFS volume, run the following commands in every VM, changing the hostname in the command for each VM:
```sh
sudo mkdir /data/sharedDirectory
sudo mount -t glusterfs VM1:/gv0 /data/sharedDirectory
``` 

Further GlusterFS information can be found at https://docs.gluster.org/en/latest/.


### 3. ActivityBucket configuration
Place the ActivityBucket JAR file in the directory as: 
- /var/activity-bucket/ActivityBucket.jar

### 4. Creating an Initialization Script
Create an initialization script with the path:
- /var/init/init.sh

Assuming that the VM containing the RabbitMQ server has an internal IP of 10.128.0.4, in each VM place the following commands inside the inicialization script, changing the hostname appropriately for the mount command:  
```sh
sudo service glusterd start
sleep 10
sudo mount -t glusterfs VM1:/gv0 /data/sharedDirectory
sudo java -jar /var/activity-bucket/ActivityBucket.jar /data/sharedDirectory 10.128.0.4:5672
```

### 5. Setup the Initialization Script
GCP allows the configuration of a VM with an initialization script, which should be done for every VM with the following command:
```sh
sudo sh /var/init/init.sh
```
