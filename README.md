# Docker container On/Offloading system
<p align="justify">
The On/Offloading system can be used to offload Docker containers from a computing device to any other resource at runtime, for example from an edge node to the cloud or vice versa. New requirements such as considering interoperability and achieving a high level of portability within heterogeneous computing frameworks currently have to be met. This is because IoT applications include different services usually distributed across interconnected, heterogeneous infrastructures including edge nodes, fog resources, micro-local, private and public clouds. As a consequence, containers running on each infrastructure provider complies with various proprietary API. Therefore, moving containers from one infrastructure to another at runtime is currently a challenge due to vendor lock-in problem, interoperability concern and similar. In order to address this challenge, you can use the Docker container On/Offloading system, which provides two components: On/Offloading Server and On/Offloading Client.
  
![Image](https://media-exp1.licdn.com/media-proxy/ext?w=800&h=800&f=n&hash=dcI1KvQON68hJgyiC4v5tfnUAP0%3D&ora=1%2CaFBCTXdkRmpGL2lvQUFBPQ%2CxAVta5g-0R6jnhodx1Ey9KGTqAGj6E5DQJHUA3L0CHH05IbfPWi_cM_YfLeipkARfitVjQAzfb61SDmwQY61eYq-e9lyiMHid5n5agYUbhl4lWdI)


## On/Offloading Server
An API is exposed by the On/Offloading Server to receive a JSON-based instruction which can be start or stop a container instance. Afterwards, the On/Offloading Server translates the instruction into a platform-dependent deployment request which is then sent to the API exposed by the On/Offloading Client.

## On/Offloading Client
An On/Offloading Client is installed on every edge resource (e.g. Raspberry Pi) or cloud infrastructure (e.g. VM). The On/Offloading Client is responsible for receiving the On/Offloading Server’s requests for the on/offloading tasks which can be start or stop containers.
</p>
<center>######################################</center><br/>

## Step 1- Install Docker and Docker's Remote API
On every edge node or cloud-based host, the Docker engine should be installed, and Docker's Remote API should be enabled. To this end, follow the instructions explained in the following page: 
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/Docker-and-Docker-Remote-API.md

## Step 2- Make sure that TCP port 10001 is open
On every edge node or cloud-based host where the On/Offloading Client will be running, TCP port `10001` should be open and accessible from the outside where the On/Offloading Server is deployed. The On/Offloading Client is listening to the port with the number of `10001` to reaceive requests (`Instantiation` or `Termination` requests) sent from the On/Offloading Server. To make sure, you can execute the following Java code on a remote host preferably outside of the On/Offloading Client's network:
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/TestIfPortIsOpenOnTheHost.java

## Step 3- Run the On/Offloading Client
On every edge node or cloud-based host, the On/Offloading Client should be deployed. To run the On/Offloading Client, the following Java code should be executed on the edge node or cloud-based host:
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/On_Offloading_Client.java

## Step 4- Pull the container image which you would like to run
On every edge node or cloud-based host where your container has to be instantiated by the Mobile Offloading Processing Microservice, the container image should be already pulled.

## Step 5- Use the On/Offloading Server's API
The On/Offloading Server provides an API to receive a JSON message which is an instruction to instantiate (Instantiation) or terminate (Termination) a container. The On/Offloading Server is now deployed here:<br/>
http://52.58.107.100:8282/onoffload/

If you would like to have your own On/Offloading Server running on your infrastructure, you can use the following command:<br/>
`docker run -p 8282:8080 -p 10001:10001 -p 10002:10002 salmant/on_offloading_server_jsi:1.2`

## Step 6- Make the JSON message
In order to make a JSON message to instantiate a container, the following code proides you a sample:
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/Instantiation.json

In order to make a JSON message to terminate a container, the following code proides you a sample:
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/Termination.json

Note 1: The JSON message should be in one line as aforementioned examples.<br/>
Note 2: In the JSON message, `HostIP` is the IP address of the resource where the container needs to be whether instantiated or terminated.<br/>
Note 3: In the JSON message, `Action` should be one of `Instantiation`, `instantiation`, `Termination` or `termination`.  
Note 4: Make sure that the JSON message is valid. To this end, different tools such as https://jsonlint.com/ can be used.<br/>
Note 5: In addition to the Web-based GUI, you can call the On/Offloading Server's API through your own program. To this end, the URL which provides the On/Offloading Server's API is: `http://52.58.107.100:8282/onoffload/api/instruction`
The following Java code is an example which calls the On/Offloading Server's API to instantiate a specific container:<br/>
https://github.com/salmant/PrEstoCloud/blob/master/OnOffloading/SendingPostRequest.java


## Step 7- Returned value sent by the On/Offloading Server's API
When the On/Offloading Server's API is called, a value will be returned that implies if the request has been successfully executed or not. The meaning of possible values returned by the On/Offloading Server's API are as follows:<br/>
`9200`: Container instantiation was successfully executed.<br/>
`9201`: Container instantiation was failed. The feasible reason could be that the container image is not already pulled on the host.<br/>
`9202`: Container instantiation was failed. The feasible reason could be that the ports required for the container instance are already occupied by other applications on the host.<br/>
`9300`: Container termination was successfully executed.<br/>
`9301`: Container termination was failed. The feasible reason could be that the container instance is not already instantiated on the host.<br/>
`9401`: Container instantiation or termination was failed. The feasible reason could be that the On/Offloading Client is not already running on the host.<br/>
`9801`: Container instantiation or termination was failed. The feasible reason could be that TCP port 10001 is not open on the host.<br/>
`java.lang.ArrayIndexOutOfBoundsException: X`: Container instantiation or termination was failed. The feasible reason could be that the JSON message does not have the correct format as provided in aforementioned examples.<br/>
`org.json.JSONException: XXXXXXXX at 1 [character Y line 1]`: Container instantiation or termination was failed. The feasible reason could be that the JSON message is not valid.<br/>

<br/><br/>



