

# Selve-Bridge

This Java based application is intendend to fill the gap between the Selve RF USB gateway and your home automation system. That said, this piece of software does the heavy lifting of communicating with the RF USB gateway by respecting the XML based messaging system as stated in the corresponding Selve specification (version **2.0.2**). It provides REST and MQTT interfaces to interact with the gateway and thus control your devices you setup.

All you need to run this server-application is a Java 8 JRE. You can easily run it as a docker container, either by compiling your own image by invoking the used maven plugin or by using the provided docker-compose setup.


# Interfaces

The Selve-Bridge provides two Interfaces to interact with:
- REST
- MQTT

## REST
The REST interface provides endpoints for controlling devices known by the gateway and additional endpoints to control and setup the gateway and devices.

*A REST API documentation will follow soon!*

**Caution:** Currently there is neither authentication nor SSL encryption implemented for the REST interface but it is planned to be added in the future.

## MQTT
You can optionally enable the MQTT interface to connect the application to an external mqtt broker. Then you can use specific topics to interact with the devices that are known by the selve gateway.

Currently only **commeo** devices, especially shutters, are supported. You can interact with them via the following topics:
- `{topic-prefix}/commeo/{id}/cmnd`
    - you can publish to this topic to send commands to the device identified by the given id
- `{topic-prefix}/commeo/{id}/state`
    - you can subscribe to this topic to get informed about state changes of the device identified by the specified id

*A documentation of the JSON payloads will follow soon!*

### Payload

- command-topic payload
    ```json
    {
        "command": "command name",
        "value": "optional value"
    }
    ``` 
- state-topic payload

# Installation

## bare metal
You can run the latest version of this application on any server that runs Java in version 8. All you need to do is to start the *.jar* file with the following command:
```sh
java -jar selve-bridge-<VERSION>.jar
```
Alternatively you can simply run the shell script `start.sh` (you can find it in the foldeer `/docker/app`) with the *.jar* file of the desired version in the same folder.

## docker-compose
Another way to run this application is in a docker container. Then you can use a pre-compiled version of this application to be run with the provided docker-compose file. For this you need `docker` and `doker-compose` to be installed on the target system. Then all you need to do is to run for example
```sh
docker-compose up
```
from within the docker folder (`/docker`).
This is the preferred way of running this application because you can easily rebuild the docker container whenever a new version of the base docker image (tag `openjdk:8`) is released. This gets important for the case when critical security issues will get fixed in trhe base image.

## self-compiled docker image
If you would like, you can compile a docker image by your own, maybe if you would like to have a single image that is compiled for your target environment. To do this you can run the
```sh
mvn clean package docker:build
```
command on the target system. This will result in a new docker image in the local docker image store that you can run with docker.

# Configuration

For the application to work properly you have to configure it accordingly. Further you can tweak and customize the application to fit your needs. The configuration will be applied by providing the corresponding environment variables.

## Connection

name|type|default|description
|-|-|-|-|
SELVEBRIDGE_CONNECTION_SERIALPORT | string | "/dev/ttyUSB0" | the serial port where the selve rf USB stick (gateway) is connected
SELVEBRIDGE_CONNECTION_RECONNECTINTERVAL | integer | 15 | seconds between two attempts to connect to the selve rf USB stick (gateway), if the connection is lost
SELVEBRIDGE_CONNECTION_WATCHDOGINTERVAL | integer | 60 | seconds between two checks for a valid connection of the selve rf USB stick (gateway), if the connection is established


## Deadlock

name|type|default|description
|-|-|-|-|
SELVEBRIDGE_DEADLOCK_THRESHOLD | integer | 60 | seconds until a blocked gateway will be interpreted as a deadlock
SELVEBRIDGE_DEADLOCK_WATCHDOGINTERVAL | integer | 30 | seconds between two checks for a possible deadlock

## MQTT

name|type|default|description
|-|-|-|-|
SELVEBRIDGE_MQTT_ENABLED | boolean | false | whether the MQTT interface should be enabled or not
SELVEBRIDGE_MQTT_BROKER | string | "hostname" | the hostname or IP address of the external mqtt broker
SELVEBRIDGE_MQTT_PROTOCOL | string | "tcp" | the protocol used to connect to the broker
SELVEBRIDGE_MQTT_PORT | integer | 1883 | the port used to connect to the broker
SELVEBRIDGE_MQTT_QOS | integer | 0 | the quality of service the messages should be published
SELVEBRIDGE_MQTT_RETAIN | boolean | true | whether the messages should be send with the retain flag set
SELVEBRIDGE_MQTT_TOPICPREFIX | string | "selve" | the topic prefix applied to subscriptions and publications


# Setup
*how to setup//maintain devices with the help of the REST interface*

# Usage

## home assistant
*sample yaml based configuration for home assistant*