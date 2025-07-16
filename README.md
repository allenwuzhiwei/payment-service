# Payment MicroService - E-commerce Backend Module

## Overview

The **Payment Service** is a core microservice in our e-commerce backend platform.  
It is responsible for handling order payments, simulating payment verification, managing user money accounts, and processing refund operations.

The service is built using **Spring Boot**, **Spring Data JPA**, and supports RESTful API interaction with Order Service and Inventory Service.  
It also applies **Factory Design Pattern** for payment method decoupling and **atomic refund logic** for secure transactions.

---

## Project Structure

The `payment-service` follows a layered **Spring Boot + JPA** architecture,  
organizing different concerns clearly into configuration, controller, service, repository, and entity layers.

- `config/`  
  ➤ Contains standard API response wrappers, global exception handling, and Swagger configuration.

- `controller/`  
  ➤ Exposes RESTful APIs for payment processing, refund creation, and account operations.

- `dto/`  
  ➤ Defines request objects like `PaymentRequest`, `RefundRequest`, and others for external input handling.

- `entity/`  
  ➤ Domain model classes:
    - `FaceDetectionLog`
    - `Payment` - represents a payment record
    - `MoneyAccount` - stores user account balances
    - `Refund` - represents refund records

- `repository/`  
  ➤ JPA Repositories for Payment, Refund, and MoneyAccount entities.

- `service/` and `service/impl/`  
  ➤ Business logic for processing payments and refunds, including payment verification and balance updates.

  ➤ Implements the **Factory Method Pattern** for handling multiple payment methods (e.g. WeChat, PayNow).

- `PaymentServiceApplication.java`  
  ➤ Main entry point of the payment microservice.

- `pom.xml`  
  ➤ Maven project configuration for dependencies and build tools.

---

## Implemented Features

### Payment Module

#### Core Functions

- Create and verify payment by order ID and amount
- Deduct balance from sender account and log the transaction
- Support multiple simulated payment methods using Factory Pattern

#### Design Pattern

- Uses **Factory Method Pattern** to decouple payment logic  
  ➤ Easily supports new methods like FaceRecognition in future

---

### Refund Module

#### Core Functions

- Create refund by `paymentId`, with reason and automatic refund amount
- Automatically credit back amount to sender account balance
- Prevents duplicate refunds by enforcing unique `paymentId`
- Query refund by `paymentId` or list all refund records

#### Extended Logic

- Ensures atomicity between refund and balance restoration
- Tracks refund status and audit fields (creator, date)

---

## Collaborators

| Name             | Role               | Description                                                                                                                                               |
|------------------|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Song Yinrui**  | Backend Developer  | Implemented payment and refund logic, applied Factory Pattern, developed all API endpoints and tested with Postman.                                       |
| **Wu Zhiwei**    | System Architect   | Designed microservice architecture and set up database structure.                                                                |

---

## Author

- **Song Yinrui**
- National University of Singapore, Institute of Systems Science (NUS-ISS)
- Development Start: **June 2025**
