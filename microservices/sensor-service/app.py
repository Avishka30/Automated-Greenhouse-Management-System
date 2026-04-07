from flask import Flask, jsonify
import py_eureka_client.eureka_client as eureka_client
from apscheduler.schedulers.background import BackgroundScheduler
import requests
import atexit

app = Flask(__name__)

# 1. Eureka Server Register
eureka_client.init(
    eureka_server="http://localhost:8761/eureka",
    app_name="SENSOR-SERVICE",
    instance_port=8082,
    instance_host="localhost",
    instance_ip="127.0.0.1"
)

BASE_URL = "http://104.211.95.241:8080/api"
latest_telemetry_data = {}
access_token = None

# Log External API and get token
def get_auth_token():
    global access_token
    try:
        response = requests.post(f"{BASE_URL}/auth/login", json={"username": "avishka", "password": "111111"})
        if response.status_code == 200:
            access_token = response.json().get("accessToken")
            print("✅ Successfully authenticated with External API")
        else:
            print("❌ Authentication failed!")
    except Exception as e:
        print("❌ Error connecting to External API auth:", str(e))

# Get telemetry data from External API and push to Automation Service
def fetch_and_push_telemetry():
    global latest_telemetry_data, access_token
    
    if not access_token:
        get_auth_token()
        if not access_token:
            return 

    headers = {"Authorization": f"Bearer {access_token}"}

    try:
        # 1. Get devices list from External API
        devices_resp = requests.get(f"{BASE_URL}/devices", headers=headers)
        
        # Token expired or unauthorized, try to get a new token and retry
        if devices_resp.status_code == 401: 
            get_auth_token()
            headers = {"Authorization": f"Bearer {access_token}"}
            devices_resp = requests.get(f"{BASE_URL}/devices", headers=headers)

        devices = devices_resp.json()
        if not devices:
            print("⚠️ No devices found in the External API.")
            return

        # 2. Get telemetry data for the first device
        # (devices registered from Zone Service will be available here)
        device_id = devices[0].get("deviceId")
        telemetry_resp = requests.get(f"{BASE_URL}/devices/telemetry/{device_id}", headers=headers)

        if telemetry_resp.status_code == 200:
            latest_telemetry_data = telemetry_resp.json()
            temp = latest_telemetry_data.get("value", {}).get("temperature")
            humidity = latest_telemetry_data.get("value", {}).get("humidity")

            print(f"🔄 Real Data Fetched -> Device: {device_id} | Temp: {temp}°C | Humidity: {humidity}%")

            # 3. Push telemetry data to Automation Service (The Pusher)
            requests.post("http://localhost:8083/api/automation/process", json=latest_telemetry_data)
        else:
            print("❌ Failed to fetch telemetry data")

    except Exception as e:
        print("❌ Error during telemetry fetch:", str(e))

# Set up a scheduler to fetch telemetry data every 10 seconds
scheduler = BackgroundScheduler()
scheduler.add_job(func=fetch_and_push_telemetry, trigger="interval", seconds=10)
scheduler.start()
atexit.register(lambda: scheduler.shutdown())


@app.route('/api/sensors/latest', methods=['GET'])
def get_latest_sensor_data():
    if not latest_telemetry_data:
        return jsonify({"message": "No telemetry data available yet"}), 404
    return jsonify(latest_telemetry_data), 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8082, debug=False)