from flask import Flask, jsonify, request
import time
import json
import random
from azure.iot.device import IoTHubDeviceClient, Message
from azure.storage.blob import BlobServiceClient
import serial
import csv
import numpy as np
import pickle

app = Flask(__name__)

app.secret_key = 'cloud-iot'



connection_string = "your connection string"
device_client = IoTHubDeviceClient.create_from_connection_string(connection_string)


def store_data_in_blob_storage(data_list):
    blob_service_client = BlobServiceClient.from_connection_string('your blob service client')
    container_name = 'new-data-container'  

    # Retrieve the container client
    container_client = blob_service_client.get_container_client(container_name)

    # Create the container if it doesn't exist
    if not container_client.exists():
        container_client.create_container()

    blob_name = f'data-{int(time.time())}.json'  # Unique blob name based on timestamp
    blob_client = container_client.get_blob_client(blob_name)

    # Convert the data to JSON format
    json_data = json.dumps(data_list)

    # Upload JSON data to Azure Blob Storage
    blob_client.upload_blob(json_data, overwrite=True)

try:
    device_client.connect()
    print("Connected to Azure IoT Hub")
except Exception as e:
    print("Error connecting to Azure IoT Hub:")
    print(str(e))

baud_rate = 9600
ser = serial.Serial('/dev/ttyACM3', baud_rate)

collected_data = []

captured_data = {}

while True:
    try:
        
        nitrogen = random.uniform(0, 100)
        phosphorus = random.uniform(0, 100)
        potassium = random.uniform(0, 100)
        ph = random.uniform(5,8)

        
        sensor_data = ser.readline().decode().strip().split(',')

       
        temperature = float(sensor_data[0])
        humidity = float(sensor_data[1])
        water_sensor = float(sensor_data[2])

        
        data = {
            'N': round(nitrogen,2),
            'P': round(phosphorus,2),
            'K': round(potassium,2),
            'temperature': round(temperature,2),
            'humidity': round(humidity,2),
            'ph':round(ph,2),
            'water_sensor': round(water_sensor,2)
        }

        message = Message(json.dumps(data))
        device_client.send_message(message)
        print("Message sent:", data)

        # Store the data in the collected_data list
        collected_data.append(data)

        if len(collected_data) == 20:
            captured_data = collected_data[len(collected_data) // 2]
            
            store_data_in_blob_storage(collected_data)

            
            collected_data = []
            break

        time.sleep(1)  

    except Exception as e:
        print("Error sending message to Azure IoT Hub:")
        print(str(e))
    



@app.route('/ml', methods = ["Get"])
def machinelearning():
    if request.method == "GET":
        my_data = list(captured_data.values())
        loaded_model = pickle.load(open('SVM', 'rb'))

        prediction = loaded_model.predict([my_data])

        return jsonify({"data": captured_data, "decision": prediction[0]})



if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)