# 🚀 Person Management System

**A powerful, extensible Spring Boot application for managing diverse person types with dynamic attributes and high-performance CSV imports.**

---

## 🌟 Overview

The **Person Management System** is a robust, scalable Spring Boot application designed to streamline the creation and management of various person types with customizable attributes. Leveraging the **Strategy design pattern**, it ensures flexibility and extensibility without touching the core codebase. The system supports **manual record creation** via REST APIs and **bulk CSV imports** of any size, optimized for **low memory usage** (≤200 MB heap) and **blazing-fast processing** (millions of records per second). Secured with **Spring Security** and backed by **transactional integrity**, it guarantees protected access and reliable data handling.

📊 **Performance Highlight**: Processes **30+ GB CSV files** with heap usage capped at **200 MB**.  
[![](https://raw.githubusercontent.com/borysmankowski/Generic-person-handler/main/Screenshot%202025-03-20%20at%2018.03.37.png)](https://github.com/borysmankowski/Generic-person-handler/blob/main/Screenshot%202025-03-20%20at%2018.03.37.png)

---

## ✨ Key Features

- **Dynamic Person Types**  
  Create and manage diverse person types with customizable attributes using the Strategy pattern.

- **High-Performance CSV Import**  
  Import massive CSV files (multiple GBs) with **heap usage ≤200 MB** and speeds of **millions of records per second**.

- **Extensibility**  
  Add new person types and attributes seamlessly by introducing new classes, keeping the core code untouched.

- **Robust Security**  
  Secure REST endpoints with **Spring Security** for protected access.

- **Transactional Integrity**  
  Fully transactional imports ensure all-or-nothing processing for data consistency.

- **Optimized Performance**  
  Designed for **low memory usage** and **high throughput** during large-scale imports.

---

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.1.4
- **Spring Security**: Endpoint protection
- **Spring Data JPA**: Database operations
- **Spring Integration**: Large-scale data processing
- **Spring AMQP**: Message-driven architecture
- **Database**: H2 (in-memory for dev/test)
- **AWS S3 SDK**: File storage/retrieval
- **Build Tool**: Maven

### Key Dependencies
- 🗺️ **ModelMapper**: Object mapping
- ✅ **Hibernate Validator**: Input validation
- 🛠️ **Lombok**: Boilerplate reduction
- 🧪 **Awaitility & AssertJ**: Testing
- 🎭 **JFairy**: Test data generation
- 🌐 **REST-assured**: API testing
- 📦 **GraalVM**: Native image support

---

## 🚀 Getting Started

### Prerequisites
- ☕ **Java 17**
- 🛠️ **Maven**
