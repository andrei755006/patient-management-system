# 🏥 Healthcare Microservices Platform

A distributed system for patient management, billing, and analytics built with a modern tech stack.

## 🏗 System Architecture
The project follows a microservices architecture pattern with an API Gateway as a single entry point.

* **API Gateway (4004):** Routing and JWT validation.
* **Auth Service:** Identity provider & token issuance.
* **Patient Service:** Core domain for patient records (PostgreSQL).
* **Billing Service:** Financial accounts management (gRPC server).
* **Analytics Service:** Event-driven data processing (Kafka consumer).

## 🛠 Tech Stack
- **Backend:** Java 21, Spring Boot 3.4.1
- **Communication:** gRPC (Protobuf), Apache Kafka
- **Infrastructure:** Docker & Docker Compose, WSL2
- **Database:** PostgreSQL
- **Security:** JWT (Stateless)
- **Mapping:** MapStruct

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose installed
- Java 21 & Maven

### Running the System
```bash
# Clone the repository
git clone <your-repo-url>

# Build and start all services
docker-compose up --build -d


📈 Current Progress
[x] API Gateway & Routing

[x] JWT Authentication flow

[x] gRPC Sync communication (Patient -> Billing)

[x] Kafka Event-driven bridge

[ ] Analytics Service (In Progress)

[ ] AWS LocalStack Deployment (Planned)