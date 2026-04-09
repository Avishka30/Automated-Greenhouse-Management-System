# Automated Greenhouse Management System (AGMS) 🌱

A cloud-native, microservices-based backend platform designed to automate greenhouse environments using IoT telemetry, a custom rule engine, and automated actions.

## 🏗️ Tech Stack

* **Infrastructure:** Eureka Service Registry, API Gateway, Spring Cloud Config Server
* **Domain Microservices:**
  * **Zone Service:** Spring Boot (Java) & PostgreSQL
  * **Sensor Service:** Python (Flask) & APScheduler
  * **Automation Service:** Node.js (Express/TypeScript)
  * **Crop Service:** Node.js (Express) & MongoDB

## ⚙️ Configuration Repository

All centralized configuration files required by the microservices are hosted in the following repository:
🔗 **[AGMS Config Repo](https://github.com/Avishka30/agms-config.git)**

## 🚀 Getting Started (Run Instructions)

To ensure smooth operation, the backend services **must** be started in the following specific order:

### Phase 1: Infrastructure Startup
1. **Config Server:** Run this first (Port 8888). It will automatically fetch configurations from the GitHub repository linked above.
2. **Service Registry (Eureka):** Run second and wait until it fully starts (Port 8761).
3. **API Gateway:** Run third. It will register with Eureka and act as the main entry point (Port 8080).

### Phase 2: Domain Microservices
Once the infrastructure is up, start the following domain services in any order:
* `zone-service` (Spring Boot)
* `crop-service` (Node.js - `npm start`)
* `automation-service` (Node.js - `npm start`)
* `sensor-service` (Python - `python app.py`)

*(Note: Verify that all services are displayed as 'UP' in the Eureka Dashboard at `http://localhost:8761` before making API calls)*

## 🧪 API Testing

A complete Postman Collection is included in the `Postman Collection` folder (`AGMS.postman_collection.json`). Import this into Postman to test the end-to-end data flow, including Zone creation, Sensor data fetching, and Automation triggers.
