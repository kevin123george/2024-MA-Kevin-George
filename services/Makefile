# Define environment variables
export DB_NAME=testDB
export DB_USERNAME=postgres
export DB_PASSWORD=asdf1234

# Default target: Start the Docker Compose services

.PHONY: smartspectest
smartspectest:
	cd smartspec\smartspectest && make

.PHONY: up
up:
	@docker-compose up -d

# Target to build the Docker images
.PHONY: build
build:
	cd DataInjectionService && .\gradlew clean build && cd .. && cd WatcherPatternService && .\gradlew clean build && cd .. && cd LocationSimulationService && .\gradlew clean build && cd .. && cd FrontEndService && .\gradlew clean build && cd .. && cd DataBaseMigrationService && .\gradlew clean build

# Target to stop the Docker Compose services
.PHONY: down
down:
	@docker-compose down

# Target to view the logs
.PHONY: logs
logs:
	@docker-compose logs -f

# Target to restart the services
.PHONY: restart
restart: down up
