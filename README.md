

# 2024-MA-Kevin-George: Data-Driven Cattle Farm Management Platform

This repository contains the codebase for a data-driven cattle farm management platform designed for anomaly detection, analytics, and simulation. The project is built around a microservices architecture and forms the technical backbone of the thesis work by Kevin George.

## Repository Structure

```
2024-MA-Kevin-George/
├── services/
│   ├── data-injection-service/
│   ├── watcher-service/
│   ├── simulation-service/
│   └── frontend-service/
├── docs/
├── README.md
└── ...
```

- **services/**: Contains the core microservices. Each subfolder is an independent Spring Boot application handling a specific responsibility (data ingestion, anomaly detection, simulation, and user interface).
- **docs/**: Documentation and supporting materials for the project.

## Prerequisites

- Java JDK 11 or higher
- Maven
- Docker & Docker Compose (optional for containerized deployments)

## Getting Started

### Clone the Repository

Clone the repository using Git:

```bash
git clone https://github.com/kevin123george/2024-MA-Kevin-George.git
cd 2024-MA-Kevin-George
```

### Navigating to the Services Folder

All the core microservices are located within the `services` directory. Change to this directory with:

```bash
cd services
```

### Running Individual Services

Each service is a standalone Spring Boot application. To run a specific service, navigate into its directory and use Maven:

For example, to run the **data-injection-service**:

```bash
cd data-injection-service
mvn spring-boot:run
```

Repeat the process for other services (e.g., `watcher-service`, `simulation-service`, `frontend-service`).

### Running the Entire Platform with Docker Compose

If you prefer to run all services together using Docker, ensure Docker and Docker Compose are installed. From the root directory of the repository, execute:

```bash
docker-compose up --build
```

This command builds and starts all the services, allowing them to communicate via pre-configured networks.

## Accessing the Application

- **Frontend UI:** Open your browser and navigate to [http://localhost:8080](http://localhost:8080)
- **Metabase Dashboard (if integrated):** [http://localhost:3000](http://localhost:3000)  
  *(Credentials for Metabase are specified in the environment configuration.)*

## Application Output

- **Real-Time Data Monitoring:** Live dashboards display cattle positions and sensor readings.
- **Anomaly Detection:** Alerts are generated when unusual patterns or outliers are detected in the sensor data.
- **Simulation Results:** The SmartSPEC simulation component allows you to run “what-if” scenarios for improved farm management.
- **Logs and Metrics:** Service logs are accessible via Docker logs or within each microservice’s console output, aiding in troubleshooting and performance monitoring.

## Testing

Run unit and integration tests for a service with:

```bash
mvn test
```

## Contributing

Contributions are welcome! Please fork the repository, create your feature branch, and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.
