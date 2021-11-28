#########################################################
## Gateway UPM v.1.0.0 ##################################
## Autor: Group5 ########################################
#########################################################


from bluepy.btle import Scanner
from datetime import datetime
import math
import random
from paho.mqtt import client as mqtt_client
import metawearLib
import threading
from time import sleep


# CONSTANCES
broker = 'localhost'
port = 1883
topic = "animalcheck/ancce/stable1"
client_id = f'python-mqtt-{random.randint(0, 1000)}'
username = 'mqttp'
password = 'ppswd123'

ptopic = "animalcheck/ancce/stable1/vet/check"

stopic = "animalcheck/ancce/stable1/vet"
sclient_id = f'python-mqtt-{random.randint(0, 1000)}'

global stext
stext = ""

DESC = "DESCONOCIDO"

# FUCTIONS
def nameByAddr(addr):
    switcher = {
        "fb:41:08:98:70:e5": "Horse2",
        "c0:33:38:11:a5:f0": "Horse1",
        "68:ab:1e:3c:6b:1d": "Horse3",
        "de:cd:c5:22:f1:f3": "Horse4",
        "bc:2e:f6:a7:ce:35": "Horse5"
    }
    return switcher.get(addr, DESC)

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("INFO: Connected publisher to MQTT Broker!")
        else:
            print("INFO: Failed publisher to connect, return code %d\n", rc)

    client = mqtt_client.Client(client_id)
    client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client

def sconnect_mqtt() -> mqtt_client:
    def on_connect(sclient, userdata, flags, rc):
        if rc == 0:
            print("INFO: Connected subscriber to MQTT Broker!")
        else:
            print("INFO: Failed subscriber to connect, return code %d\n", rc)

    sclient = mqtt_client.Client(sclient_id)
    sclient.username_pw_set(username, password)
    sclient.on_connect = on_connect
    sclient.connect(broker, port)
    return sclient

def subscribe(sclient: mqtt_client):
    def on_message(sclient, userdata, msg):
        global stext
        stext = msg.payload.decode()
        print(" VET SERVICE: %s needs a revision, sending a light signal to device..." % (stext))

    sclient.subscribe(stopic, 1)
    sclient.on_message = on_message


# MAIN PROGRAM
client = connect_mqtt()
client.loop_start()

sclient = sconnect_mqtt()
subscribe(sclient)
sclient.loop_start()

contador = 0
h1_weight = 400
h2_weight = 420
h3_weight = 415
h4_weight = 405
h5_weight = 407

h1_age = 5
h2_age = 7
h3_age = 9
h4_age = 2
h5_age = 10

h1_count = 0
h2_count = 0
h3_count = 0
h4_count = 0
h5_count = 0
sleep(1)

while 1:

    now = datetime.now()
    dt_string = now.strftime("\n%d/%m/%Y %H:%M:%S")
    print(dt_string, "scanning [%d]" %contador)
    contador +=1

    scanner = Scanner()
    devices = scanner.scan(10.0)

    for dev in devices:
        
        habitacion = nameByAddr(dev.addr)
        
        if habitacion == "Horse1":
            h1_intensity = dev.rssi
        if habitacion == "Horse2":
            h2_intensity = dev.rssi
        if habitacion == "Horse3":
            h3_intensity = dev.rssi
        if habitacion == "Horse4":
            h4_intensity = dev.rssi
        if habitacion == "Horse5":
            h5_intensity = dev.rssi

        if habitacion!=DESC:
            if habitacion == "Horse1":
                random_number = random.uniform(37.0, 39.0)
                h1_temp = round(random_number, 1)
                h1_heart = random.randint(28, 40)
                if stext == "Horse1":
                    print(" VET SERVICE: %s found, the device is lighting" % (stext))
                    msg = f"{habitacion} Success"
                    result = client.publish(ptopic, msg, 1)
                    stext = ""
                    h1_count = 0
                print(" On Stable 1 %s: signal intensity %s, weight %s, temperature %s, age %s, heart rate %s" % (habitacion, h1_intensity, h1_weight, h1_temp, h1_age, h1_heart))
                msg = f"{habitacion} {h1_intensity} {h1_weight} {h1_temp} {h1_age} {h1_heart}"
                result = client.publish(topic, msg, 0)
            if habitacion == "Horse2":
                random_number = random.uniform(37.0, 38.5)
                h2_temp = round(random_number, 1)
                h2_heart = random.randint(28, 40)
                if stext == "Horse2":
    
                    try:
                        t = threading.Thread(target = metawearLib.getInfo, args=(dev.addr,))
                        t.start()
                    except:
                        print("  >>> Unexpected error MetaWear API <<<")
                        
                    print(" VET SERVICE: %s found, the device is lighting" % (stext))
                    msg = f"{habitacion} Success"
                    result = client.publish(ptopic, msg, 1)
                    stext = ""
                    h2_count = 0
                print(" On Stable 1 %s: signal intensity %s, weight %s, temperature %s, age %s, heart rate %s" % (habitacion, h2_intensity, h2_weight, h2_temp, h2_age, h2_heart))
                msg = f"{habitacion} {h2_intensity} {h2_weight} {h2_temp} {h2_age} {h2_heart}"
                result = client.publish(topic, msg, 0)
            if habitacion == "Horse3":
                random_number = random.uniform(37.0, 38.5)
                h3_temp = round(random_number, 1)
                h3_heart = random.randint(28, 40)
                if stext == "Horse3":
                    print(" VET SERVICE: %s found, the device is lighting" % (stext))
                    msg = f"{habitacion} Success"
                    result = client.publish(ptopic, msg, 1)
                    stext = ""
                    h3_count = 0
                print(" On Stable 1 %s: signal intensity %s, weight %s, temperature %s, age %s, heart rate %s" % (habitacion, h3_intensity, h3_weight, h3_temp, h3_age, h3_heart))
                msg = f"{habitacion} {h3_intensity} {h3_weight} {h3_temp} {h3_age} {h3_heart}"
                result = client.publish(topic, msg, 0)
            if habitacion == "Horse4":
                random_number = random.uniform(37.0, 38.5)
                h4_temp = round(random_number, 1)
                h4_heart = random.randint(28, 40)
                if stext == "Horse4":
                    print(" VET SERVICE: %s found, the device is lighting" % (stext))
                    msg = f"{habitacion} Success"
                    result = client.publish(ptopic, msg, 1)
                    stext = ""
                    h4_count = 0
                print(" On Stable 1 %s: signal intensity %s, weight %s, temperature %s, age %s, heart rate %s" % (habitacion, h4_intensity, h4_weight, h4_temp, h4_age, h4_heart))
                msg = f"{habitacion} {h4_intensity} {h4_weight} {h4_temp} {h4_age} {h4_heart}"
                result = client.publish(topic, msg, 0)
            if habitacion == "Horse5":
                random_number = random.uniform(37.0, 38.5)
                h5_temp = round(random_number, 1)
                h5_heart = random.randint(28, 40)
                if stext == "Horse5":
                    print(" VET SERVICE: %s found, the device is lighting" % (stext))
                    msg = f"{habitacion} Success"
                    result = client.publish(ptopic, msg, 1)
                    stext = ""
                    h5_count = 0
                print(" On Stable 1 %s: signal intensity %s, weight %s, temperature %s, age %s, heart rate %s" % (habitacion, h5_intensity, h5_weight, h5_temp, h5_age, h5_heart))
                msg = f"{habitacion} {h5_intensity} {h5_weight} {h5_temp} {h5_age} {h5_heart}"
                result = client.publish(topic, msg, 0)
    
    if stext == "Horse1":
        h1_count = h1_count + 1
        if h1_count > 1:
            print(" VET SERVICE: %s does not found, the device is too far or off, try again later" % (stext))
            msg = f"{stext} Fail"
            result = client.publish(ptopic, msg, 1)
            stext = ""
            h1_count = 0
    if stext == "Horse2":
        h2_count = h2_count + 1
        if h2_count > 1:
            print(" VET SERVICE: %s does not found, the device is too far or off, try again later" % (stext))
            msg = f"{stext} Fail"
            result = client.publish(ptopic, msg, 1)
            stext = ""
            h2_count = 0
    if stext == "Horse3":
        h3_count = h3_count + 1
        if h3_count > 1:
            print(" VET SERVICE: %s does not found, the device is too far or off, try again later" % (stext))
            msg = f"{stext} Fail"
            result = client.publish(ptopic, msg, 1)
            stext = ""
            h3_count = 0
    if stext == "Horse4":
        h4_count = h4_count + 1
        if h4_count > 1:
            print(" VET SERVICE: %s does not found, the device is too far or off, try again later" % (stext))
            msg = f"{stext} Fail"
            result = client.publish(ptopic, msg, 1)
            stext = ""
            h4_count = 0
    if stext == "Horse5":
        h5_count = h5_count + 1
        if h5_count > 1:
            print(" VET SERVICE: %s does not found, the device is too far or off, try again later" % (stext))
            msg = f"{stext} Fail"
            result = client.publish(ptopic, msg, 1)
            stext = ""
            h5_count = 0
            
    print("END SCANNING")
    sleep(1)
    msg = f"END SCANNING"
    result = client.publish(topic, msg, 0)
               
                


print("----------------------\nEND Gateway")
