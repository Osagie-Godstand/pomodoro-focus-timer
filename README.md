# pomodoro-timer
A simple Pomodoro timer CLI ASCII application built using the Spring Boot framework.

## Automating Program Compilation with a Makefile
- To build target simply use: make build
- To run target simply use: make run

## Verbose Build and Run Command
- mvn clean package
- java -jar /Users/dgodstand/java/pomodoro-focus-timer/pomodoro-timer/target/pomodoro-timer-0.0.1-SNAPSHOT.jar

## The 2 endpoints 
- http://localhost:8080/start
- http://localhost:8080/stop

## To Start and Stop from the terminal use 
- curl http://localhost:8080/start
- curl http://localhost:8080/stop

