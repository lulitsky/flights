#Upday Coding Task

## Implementation details
The application is built as spring-boot service.
There are 2 main challenges in the project - to create the valid flight schedule and to develop the REST API to access the schedule data:

### Building the schedule
FlightScheduleBuildingService is responsible for building the flight schedule for the day. It has one public method and performs the task recursively.
The logic of building the schedule is described in the method comments in the FlightScheduleBuildingService class.
The input data - flights to be taken and home bases of the aircraft are stored i the CSV files in resources directory.
I had to change one of the inputs to get the valid results - see detailed explanation below. 

### REST API for the schedule
The API is versioned as "v1" for future extendability.  (The version is not included to the REST API paths, to match the original required path).
 The FlightsController defined the API, the business logic is handled in FlightService. Mapstruct framework is used for mapping operations between internal model classes and resources exposed in the API.

## Execution Results
When I ran the application with the provided input, no valid schedule could be created. I added, as described above, the additional flexibility, 
by adding the possibility to move the free aircraft to another airport, to pick up the flight, that could not be ciovered by other way, but it didn't help to 
create the solution, though alloed to remove just one flight (LHR to TXL at 17:00 ) to get the valid, coverable schedule.
So, in the code i included the resources flights_original.csv with the original flights input and flights.csv, with one flöight that we had to "cancel".
Taken flights.csv as the input, the application ran and returned the valid results after calling the APIs.

## Testing
I created the unit tests for Service level and unit tests for the non-trival constructors of the model classes.
Also, I created the integration test, that tries to build the valid schedule, based on the original input and fails.

## Used frameworks and libraries
I implemented the task, as the spring-boot service
Mapstruct framework is used for mapping operations between internal model classes and resources exposed in the API.
slf4j is used for logging and lombock library for easy logging setup.
Maven is used for build and running the service

## Build and Execution
To build the application run "mvn clean install" command.
To start it, run "mvn spring-boot:run", after the successfull start up, you can issue API calls as follows:
* GET http://localhost:8080/flightplan - get entire flight plan 
* GET http://localhost:8080/flightplan?airport={code} - get flight plan for specific airport
* GET http://localhost:8080/operationsplan?registration={code} - get opetaions plan for specific aircraft
