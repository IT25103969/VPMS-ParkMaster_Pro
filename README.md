# Vehicle Parking Management System

A web-based application to manage parking slots, track vehicle entry/exit, and calculate fees.

## Technology Stack
-   **Backend:** Java (Spring Boot 3.x)
-   **Frontend:** HTML5, CSS3, Vanilla JavaScript
-   **Database:** H2 In-Memory Database

## Prerequisites
-   Java 17 or higher
-   Maven (or use an IDE like IntelliJ IDEA / Eclipse)

## Project Structure
```
parking-system/
├── src/main/java/       # Backend Source Code
├── src/main/resources/  # Configuration & Static Files (Frontend)
└── pom.xml              # Maven Dependencies
```

## How to Run

1.  **Open Terminal** (PowerShell or Command Prompt).
2.  **Navigate** to the project directory:
    ```sh
    cd parking-system
    ```
3.  **Run the Application**:
    ```sh
    mvn spring-boot:run
    ```
    *(If you don't have Maven installed globally, you can open this project in IntelliJ IDEA or Eclipse and run `ParkingApplication.java`)*

4.  **Access the App**:
    Open your browser and go to: [http://localhost:8080](http://localhost:8080)

## Features
-   **Dashboard:** View all parking slots (Green = Free, Red = Occupied).
-   **Park Vehicle:** Click a green slot or use the button to park a vehicle. The system automatically assigns a slot.
-   **Exit Vehicle:** Click a red slot to process exit and calculate the fee ($10/hour).
-   **History:** View past transactions.

## Database Console
To view the raw data, access the H2 Console:
-   URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
-   JDBC URL: `jdbc:h2:mem:parkingdb`
-   User: `sa`
-   Password: `password`
