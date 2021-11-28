# AnimalCheck
Final project of the Distributed Systems for IoT subject from Internet of Things university master's degree: https://masteriot.etsist.upm.es

## The project includes the following devices:

- Raspberry Pi 3 model B (Gateway)
- Smart watches/band with Bluetooth, one is a wrist band from Metawear (Beacons)
 
## The project includes the following functionalities:

- The gateway will scan all the Bluetooth devices near it.
- The gateway will detect the previously stored ones by the bluetooth's MAC.
- The gateway will send the fake information about an animal from the detected beacons through MQTT.
- The mobile app will recive the information through MQTT.
- If the mobile app recive a parameter out of bounds about the animal will send an alert saying that an animal is in danger.
- The mobile app can send a message through MQTT saying the animal that wants to be located.
- The gateway will recive the message and will search for the device of the animal.
- The gateway will send a message saying if it was found or wasn't found. (If it was found and was the Metawear device will light a led that the band has).
- The mobile app will recive the message and will alert the user if it was found or not.
