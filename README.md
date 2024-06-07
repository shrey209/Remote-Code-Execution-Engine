# Remote Code Execution Engine

This project is a remote code execution engine designed to execute code snippets in a sandboxed environment. It supports executing Python and C++ code and delivers the output via WebSockets or a REST API. The project leverages various technologies including Spring Boot, Kafka, WebSockets, Python, FastAPI, and Docker for sandboxing.

## Table of Contents
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
  - [WebSocket Usage](#websocket-usage)
  - [REST API Usage](#rest-api-usage)
- [API Endpoints](#api-endpoints)
- [Sample JSON](#sample-json)
- [Contributing](#contributing)

## Tech Stack
- **Spring Boot**: For API and Kafka, WebSocket integration
- **Kafka**: As message queues
- **WebSockets**: To deliver the output
- **Python**: For creating the executor API using Docker library and FastAPI
- **Docker**: For sandboxing

## Features
- Execute code snippets in a secure, isolated environment.
- Supports Python and C++ languages.
- Delivers output through WebSockets or a REST API.
- Uses Kafka for message queuing.

## Prerequisites
- Docker Engine
- Kafka Server
- Java Development Kit (JDK)
- Python 3.x
- Postman (for testing)

## Installation
1. **Clone the repository**:
    ```sh
    git clone https://github.com/your-username/remote-code-execution-engine.git
    cd remote-code-execution-engine
    ```

2. **Start the Kafka server**:
    - If you don't have Kafka installed, use the provided Docker file to run a Kafka container.
    - Example Docker command:
      ```sh
      docker-compose up -d kafka
      ```

3. **Run the Python executor**:
    ```sh
    cd python-rce
    python remote_code_execution_engine.py
    ```

4. **Run the Spring Boot application**:
    ```sh
    cd ../spring-application
    ./mvnw spring-boot:run
    ```

## Usage
### WebSocket Usage
1. **Connect to the WebSocket**:
    - Use Postman or any WebSocket client to connect to `ws://localhost:8080/ws`.
    - **On connecting, you will receive a UUID**.

2. **Send Code Execution Request**:
    - **Add the received UUID** to the JSON payload and send the request to the WebSocket connection:
      ```json
      {
          "uuid": "<received-uuid>",
          "code": "<your-code>",
          "input_data": "<input-data>",
          "lang": "<language>"
      }
      ```
    - Example for Python:
      ```json
      {
          "uuid": "received-uuid",
          "code": "print('Hello, World!')",
          "input_data": "",
          "lang": "python"
      }
      ```

3. **Receive Output**:
    - The output will be shown and the connection will be closed.

### REST API Usage
1. **Send Code Execution Request**:
    - Use Postman or any HTTP client to send a POST request to `http://127.0.0.1:8001/code`.
    - Send a JSON payload with the following format:
      ```json
      {
          "code": "<your-code>",
          "input_data": "<input-data>",
          "lang": "<language>"
      }
      ```
    - Example for C++:
      ```json
      {
          "code": "#include <iostream>\nint main() { std::cout << 'Hello, World!'; return 0; }",
          "input_data": "",
          "lang": "cpp"
      }
      ```

2. **Receive Output**:
    - The output will be returned in the response.

## API Endpoints
- **WebSocket Endpoint**: `ws://localhost:8080/ws`
- **REST API Endpoint**: `http://127.0.0.1:8001/code`

## Sample JSON
Here's a sample JSON file you can use to test the code execution engine:

### sample-code.json
```json
{
    "uuid": "sample-uuid-1234",
    "code": "#include <iostream>\nint main() { std::cout << 'Hello, World!'; return 0; }",
    "input_data": "",
    "lang": "cpp"
}
