import express from 'express';
import type { Request, Response } from 'express';
import axios from 'axios';
import { Eureka } from 'eureka-js-client';

const app = express();
app.use(express.json());

const PORT = 8083;

// --- 1. Types & Interfaces (TypeScript) ---
interface TelemetryData {
    deviceId: string;
    zoneId: string;
    value?: {
        temperature: number;
        tempUnit: string;
        humidity: number;
        humidityUnit: string;
    };
    capturedAt: string;
}

interface Zone {
    id: number;
    name: string;
    minTemp: number;
    maxTemp: number;
    deviceId: string;
}

interface ActionLog {
    timestamp: string;
    temperature: number;
    action: string;
}

const actionLogs: ActionLog[] = [];

// --- 2. Eureka Server Register ---
const client = new Eureka({
  instance: {
    app: 'AUTOMATION-SERVICE',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    statusPageUrl: `http://localhost:${PORT}`,
    port: {
      '$': PORT,
      '@enabled': true,
    },
    vipAddress: 'AUTOMATION-SERVICE',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
  },
});

client.start();

// --- 3. (Rule Engine) ---
// [cite: 143, 149]
app.post('/api/automation/process', async (req: Request, res: Response): Promise<any> => {
  const telemetryData: TelemetryData = req.body;
  
  const currentTemp = telemetryData.value?.temperature;

  if (!currentTemp) {
      return res.status(400).json({ error: "Temperature data is missing" });
  }

  console.log(`\n📥 Received Telemetry: Temp = ${currentTemp}°C`);

  try {
    // Get all zones from the Zone Service (via Eureka) [cite: 144]
    const zoneResponse = await axios.get<Zone[]>('http://localhost:8081/api/zones');
    const zones = zoneResponse.data;

    if (zones.length === 0) {
      console.log("⚠️ No zones configured in the database!");
      return res.status(404).json({ message: "No zones found" });
    }

    // Search for the zone matching the deviceId from telemetry, or default to the first zone [cite: 145]
    const targetZone = zones.find(z => z.deviceId === telemetryData.deviceId) ?? zones[0];
    if (!targetZone) {
      return res.status(404).json({ message: "No matching zone found" });
    }
    const minTemp = targetZone.minTemp;
    const maxTemp = targetZone.maxTemp;

    console.log(`📊 Zone Thresholds -> Min: ${minTemp}°C | Max: ${maxTemp}°C`);

    let action = "NORMAL - NO ACTION NEEDED";

    // Brain of the Rule Engine: Decide action based on temperature thresholds [cite: 146, 148]
    if (currentTemp > maxTemp) {
      action = "TURN_FAN_ON"; 
    } else if (currentTemp < minTemp) {
      action = "TURN_HEATER_ON";
    }

    console.log(`⚙️ ACTION TRIGGERED: ${action}`);

    actionLogs.push({
      timestamp: new Date().toISOString(),
      temperature: currentTemp,
      action: action
    });

    res.status(200).json({ message: "Processed", action: action });

  } catch (error: any) {
    console.error("❌ Error communicating with Zone Service:", error.message);
    res.status(500).json({ error: "Internal Server Error" });
  }
});

// --- 4. Logs Endpoint එක --- [cite: 150]
app.get('/api/automation/logs', (req: Request, res: Response) => {
  res.status(200).json(actionLogs);
});

app.listen(PORT, () => {
  console.log(`🚀 Automation Service (TypeScript) is running on port ${PORT}`);
});