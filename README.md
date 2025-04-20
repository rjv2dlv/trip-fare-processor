# trip-fare-processor
Calculates trip fares based on the fare charges configurations and the tap on and off input provided, to produce the output in the csv format.

## Overview

This project processes tap-on and tap-off events from a bus fare system to generate a summary of customer trips and calculate corresponding fares. 
It uses a configurable fare from an input file and outputs the final trips in CSV format, including trip duration, fare amount, and trip status (e.g., COMPLETED, INCOMPLETE, CANCELLED).


## Assumptions

1. Every TapOff event has a corresponding TapOn event. Any TapOff without a matching TapOn will be ignored.
2. Fares are symmetric. Eg: traveling from StopA to StopB costs the same as StopB to StopA.
3. Lombok is used to reduce boilerplate code (e.g., getters, setters, logging).
4. BigDecimal is used for fare calculations to ensure precision, even though it's not the most performant data type.
5. The input file is well-formed and is not missing data.


## How to Build and Run the Project

Ensure you have Maven and Java 11+ installed.

1. **Clean the project**
   ```mvn clean```
2. **Run the tests**
    ```mvn test```
3. **Build the project**
   ```mvn package```
4. **Run the application**
   ```java -jar target/faresystem-1.0-SNAPSHOT.jar```


## Test Harness

The project includes a JUnit-based test suite to validate fare calculations, trip parsing, and CSV generation.

**To run the tests**
```mvn test```

## Input, Output, trip fares and config file paths

### Input file:
The input file path can be provided in the application.properties file in the field: ```input.file.name```
The default input file path and file name is ```src/main/resources/input/taps.csv```

### Output file:
The output file path can be provided in the application.properties file in the field: ```output.file.name```
The default output file path and file name is ```src/main/resources/output/trips.csv```

### Trip fares file:
The trip fares can be configured under a csv file. The file path can be provided in the application.properties file in the field: ```trip.fares.file.name```
The default trip fares file path and file name is ```src/main/resources/input/trip-fares.csv```

### application.properties file:
The application.properties file contains configurations for input, output, trip fares files.
It can be extended further to add any configurations that maybe required for the application to use.


## Sample Files

### Fare Configuration (`fare_config.csv`)
    from, to, fare
    Stop1, Stop2, 3.25
    Stop2, Stop3, 5.50
    Stop1, Stop3, 7.30
    Stop3, Stop4, 11.50
    Stop1, Stop4, 8.90
    Stop2, Stop5, 9.90

### Tap Input (`taps.csv`)
    ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
    1, 22-01-2023 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559
    2, 22-01-2023 13:05:00, OFF, Stop2, Company1, Bus37, 5500005555555559
    3, 22-01-2023 09:20:00, ON, Stop3, Company1, Bus36, 4111111111111111
    4, 23-01-2023 08:00:00, ON, Stop1, Company1, Bus37, 4111111111111111
    5, 23-01-2023 08:02:00, OFF, Stop1, Company1, Bus37, 4111111111111111
    6, 24-01-2023 16:30:00, OFF, Stop2, Company1, Bus37, 5500005555555559
    7, 24-01-2023 17:30:00, ON, Stop4, Company1, Bus37, 5500005555555559
    8,
    9, 24-01-2023 11:30:00, OFF, Stop4, Company1, Bus37, 5500005555555558
    10, 24-01-2023 11:28:00, ON, Stop1, Company1, Bus37, 5500005555555558
    11, 24-01-2023 17:30:00, OFF, Stop4, Company1, Bus37, 5500005555555557
    12, 24-01-2023 17:30:00, ON, Stop4, Company1, Bus37
    13, 24-01-2023 17:30:00, ON, Stop5, Company1, Bus37, 5500005555555556
    14, 24-01-2023 17:35:00, ON, Stop5, Company1, Bus37, 5500005555555556
    15, 24-01-2023 17:38:00, ON, Stop5, Company1, Bus37, 5500005555555556

### Output (`trips.csv`)
    Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status
    22-01-2023 13:00:00,22-01-2023 13:05:00,300,Stop1,Stop2,$3.25,Company1,Bus37,5500005555555559,COMPLETED
    24-01-2023 17:30:00,,0,Stop4,,$11.50,Company1,Bus37,5500005555555559,INCOMPLETE
    22-01-2023 09:20:00,,0,Stop3,,$11.50,Company1,Bus36,4111111111111111,INCOMPLETE
    23-01-2023 08:00:00,23-01-2023 08:02:00,120,Stop1,Stop1,$0,Company1,Bus37,4111111111111111,CANCELLED
    24-01-2023 17:30:00,,0,Stop5,,$9.90,Company1,Bus37,5500005555555556,INCOMPLETE
    24-01-2023 17:35:00,,0,Stop5,,$9.90,Company1,Bus37,5500005555555556,INCOMPLETE
    24-01-2023 17:38:00,,0,Stop5,,$9.90,Company1,Bus37,5500005555555556,INCOMPLETE
    24-01-2023 11:28:00,24-01-2023 11:30:00,120,Stop1,Stop4,$8.90,Company1,Bus37,5500005555555558,COMPLETED


## Requirements

- Java 11 or higher
- Maven 3.x

