# Overview

The Person Management System is a Spring Boot application designed to create and manage various types of people with customizable attributes. It supports manual creation of person records and bulk import from CSV files of any size, with high performance and low memory usage. The system is extensible, allowing new person types and attributes to be added without modifying the core codebase, thanks to the Strategy design pattern. Security is enforced using Spring Security, and transactional handling ensures data integrity during imports.

# Features

Dynamic Person Types: Create and manage different types of people with customizable attributes using the Strategy pattern.

CSV Import: Import person records from CSV files of any size (multiple GB) with heap usage capped at 200 MB and processing speeds of several million records per second.

Extensibility: Add new person types and attributes by introducing new classes, without altering core code.

Security: Endpoints are secured using Spring Boot Security to ensure protected access.

Transactional Integrity: Imports are fully transactional, ensuring all records are processed or rolled back in case of errors.

High Performance: Optimized for low memory usage and high throughput during large-scale data imports.

### Tech Stack


Java: 17

Spring Boot: 3.1.4

Spring Security for endpoint protection

Spring Data JPA for database operations

Spring Integration for handling large-scale data processing

Spring AMQP for message-driven architecture

Database: H2 (in-memory, for development/testing)

### Dependencies:

ModelMapper for object mapping

Hibernate Validator for input validation

Lombok for reducing boilerplate code

Awaitility and AssertJ for testing

JFairy for generating test data

REST-assured for API testing

Build Tool: Maven





Java 17



Maven
