# Workflow Based Model for Paralel and Distributed Processing
The contents of this repository contain the source code and utilitary scripts developted for the prototype and experimentation examples done during the writing of my master thesis in the area of Computer Science and Engineering in the academic institution belonging to the Polytechnic of Lisbon, Instituto Superior de Engenharia de Lisboa (ISEL). 

The work done in this thesis is a contribution to finding answers to some open issues, proposing a workflow model with the following characteristics: 
    i) decentralization of control over the execution of multiple tasks in a workflow; 
    ii) execution of a workflow with multiple iterations; 
    iii) possibility of specifying replicas of a task to support load balancing; 
    iv) encapsulation of tasks in containers, enabling their execution on multiple computing nodes; 
    v) flexible specification of workflows  independently of the technological infrastructure for its execution.


The prototype for experimentation runs on a computing infrastructure that uses virtual machines, forming a virtual cluster with multiple computing nodes (virtual machines) hosted on the Google Cloud Platform. Multiple computing nodes share files through the Gluster distributed file system. The multiple tasks of a workflow, activated in the context of autonomous components called Activities, are executed encapsulated in Docker containers, allowing great flexibility in the development and reuse of these Tasks in multiple application cases.


