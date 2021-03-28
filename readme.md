

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

## Mountpoint of selve gateway

Since in some installations there are multiple usb devices you cannot reliably say what ttyUSB number the selve gateway will be applied to. In this situation giving you selve gateway a symbolic name when it gets mounted is a good way to work around this. To do this you first have to identify the serial number of your selve gateway by issueing the following command (replace the *ttyUSB0* by whatever device name your connected selve gateway is mounted at)
```sh
$ udevadm info --query=property --name=/dev/ttyUSB0 | grep SERIAL
```
This will give you a result like this:
```sh
ID_SERIAL=FTDI_FT230X_Basic_UART_<SERIAL_NUMBER>
ID_SERIAL_SHORT=<SERIAL_NUMBER>
```
Next step is to create a udev rule to assign a symbolic name to the device with this serial number everytime when it will be mounted. To do this create a file `/etc/udev/rules.d/99-selvegateway.rules` with the following content:
```
ACTION=="add",ENV{ID_BUS}=="usb",ENV{ID_SERIAL_SHORT}=="<SERIAL_NUMBER>",SYMLINK+="selve-gateway",MODE=0666
```
Maybe you have to reload udev rules by issueing the command
```sh
udevadm control --reload-rules
```

If all went well you will have a symbolic link `/dev/selve-gateway` that will link to the corresponding `/dev/ttyUSBx` everytime you connect your selve gateway.

HINT: This approach will not work with the docker-compose way of starting the application, due to limitations of docker-compose.

## bare metal
You can run the latest version of this application on any server that runs Java in version 8. All you need to do is to start the *.jar* file with the following command:
```sh
java -jar selve-bridge-<VERSION>.jar
```
Alternatively you can simply run the shell script `start.sh` (you can find it in the foldeer `/docker/app`) with the *.jar* file of the desired version in the same folder.

## docker
you have multiple ways of running the selve-bridge application as a docker container.

## docker run script
You can simply change to the `docker/app` folder and execute the `docker-run.sh` script, that will build the required docker image `adoptopenjdk-8` (a debian base image with the latest java 8 version of adoptopenjdk) and start the container. Maybe you have to edit the script to apply customization, e.g. you use another device name for the selve gateway.

## docker-compose
Another way to run this application is in a docker container. Then you can use a pre-compiled version of this application to be run with the provided docker-compose file. For this you need `docker` and `doker-compose` to be installed on the target system. Then all you need to do is to run for example
```sh
docker-compose up
```
from within the docker folder (`/docker`).




# Configuration

For the application to work properly you have to configure it accordingly. Further you can tweak and customize the application to fit your needs. The configuration will be applied by providing the corresponding environment variables.

Alternatively you can set these values by creating/editing the `application.yml` file in the `docker/app/config/` folder.

## Connection

With these properties you can configure the connection to the selve gateway, that is the USB hardware.

name|type|default|description
|-|-|-|-|
SELVEBRIDGE_CONNECTION_SERIALPORT | string | "/dev/ttyUSB0" | the serial port where the selve rf USB stick (gateway) is connected
SELVEBRIDGE_CONNECTION_RECONNECTINTERVAL | integer | 15 | seconds between two attempts to connect to the selve rf USB stick (gateway), if the connection is lost
SELVEBRIDGE_CONNECTION_WATCHDOGINTERVAL | integer | 60 | seconds between two checks for a valid connection of the selve rf USB stick (gateway), if the connection is established


## Deadlock

Due to the limitations, that the gateway can only handle one command at a time, this application will lock the selve gateway when issueing a command until the gateway return the expected response. To prevent a deadlock in case of an unexpected error, a deadlock watchdog will periodically check for a deadlock and will fix it, when one found. A deadlock in this case means, that the device is locked longer than the specified deadlock-threshold.

name|type|default|description
|-|-|-|-|
SELVEBRIDGE_DEADLOCK_THRESHOLD | integer | 60 | seconds until a blocked gateway will be interpreted as a deadlock
SELVEBRIDGE_DEADLOCK_WATCHDOGINTERVAL | integer | 30 | seconds between two checks for a possible deadlock

## MQTT

You can enable and cofigure the connection the an external MQTT broker to be able to talk to this application via MQTT.

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
*how to setup/maintain devices with the help of the REST interface*

# Usage

Because this app offers a MQTT interface you can easily integrate your selve devices in any of the well known smart home systems (Home Assistant, Fhem, OpenHab, etc.). You just have to configure the MQTT topics correctly.

## home assistant

```yaml
cover:
  - platform: mqtt
    name: "living room"
    command_topic: "selve/commeo/0/cmnd"
    position_topic: "selve/commeo/0/state"
    set_position_topic: "selve/commeo/0/cmnd"
    set_position_template: '{"command":"DRIVE_POS","value": {{ position }}}'
    qos: 0
    retain: true
    payload_open: '{"command":"DRIVE_UP"}'
    payload_close: '{"command":"DRIVE_DOWN"}'
    payload_stop: '{"command":"STOP"}'
    position_open: 100
    position_closed: 0
    optimistic: false
    value_template: '{{ value_json["position"] }}'
```