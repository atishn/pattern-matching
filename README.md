# This workspace belongs to Command Line Pattern-Matching Processor.

This application reads the patterns and paths from an input file and output the closest matching pattern for the given path in the output file.

## Stack
 1. Maven 3 (`brew install maven`)
 2. Java 8 ([download site](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
 3. Spring Boot (part of maven build, no install necessary)

## Build Steps
 1. build and test `mvn clean install`
 2. run the app `java -jar target/pattern-matching-path-0.0.1-SNAPSHOT.jar input.txt output.txt`

## Notes:
 1. This application is being developed using Spring-Boot Java Stack.
 2. This application is being developed with Testing approach. Integration tests were considered during development.
 3. This is the single threaded application.

## Complexity
 1. Algorithmic complexity for the shorter version of input file, (n - number of patterns/paths & m - avg, number of character in each of them) would be n*m
 2. For very large number of inputs, the alogrithmic complexity would be tend to ~ n.
 3. This is pretty linear program algorithmic side. The application execution time increases linearly for the increase in number of patterns or number of paths.

# Assumption:
1. The program assumes Incoming patterns and paths are well-formed. It records the exception if any pattern is blank, otherwise ignores it silently.
2. Currently, the program supports wildcard entry single asterisk '*' only. If wildcard comes along with any other character, then it will be considered as regular pattern block.


## Future Improvements.
 1. Introduce Unit Tests.
 2. Work on edge cases.
 3. Introduce code coverage tool like Jacoco to maintain code coverage.
 4. Add Jmeter profile to src/test/jmeter.


## Local dev setup
 1. Install [IntelliJ Ultimate](https://www.jetbrains.com/idea/download/). IT can provide a license.
 2. Install Sprint Boot plugins (IntelliJ > Preferences > Plugins)
 3. Install checkstyle-idea plugins (IntelliJ > Preferences > Plugins)
 4. To run/debug the app from IDE, select CrawlerBootApplication class, right-click and run or debug.

## Steps
 1. Code Compile. `mvn clean compile`
 2. Run Unit Test `mvn clean test`
 3. Run Integration Test `mvn clean verify`
 4. Make Build `mvn clean install`