# Music Service

## Technical Stack
- Java 17
- SpringBoot (Webflux, Reactive, Cache)
- Resilience4j (CircuitBreaker, RateLimiter, TimeLimiter)
- Gradle
- Docker
- Swagger OpenAPI

## Build
To build the application,
```
./gradlew clean build
```

To build the docker image,
```
./gradlew clean build
docker build -t musicservice .
```

## Run

To run using Gradle,
```
./gradlew bootRun
```
To run using docker,
```
docker run -p 8080:8080 musicservice
```

## Sample MBIDs
- f27ec8db-af05-4f36-916e-3d57f91ecf5e
- 1f9df192-a621-4f54-8850-2c5373b7eac9
- 34394522-a0f0-4675-aad2-30ac3cb3d7d3
- e0bba708-bdd3-478d-84ea-c706413bedab

## Swagger Documentation
The swagger UI is available at
```
http://localhost:8080/musify/swagger-ui.html
```
and api documentation is available at
```
http://localhost:8080/musify/api-docs
```

## Metrics
The metrics and prometheus endpoints are available at
```
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/promotheus
```

## Solution Description
WebFlux/Reactive streams was chosen to support non-blocking architecture. Resilience4j library was used to rate limit, 
circuit break to avoid queues in case of repeated failures. I have used the default cache provider with MBID as key. 
I have used OpenAPI documentation to document the REST API and Gradle for dependency management and build.

### Possible Improvements
Redis or some other distributed cache can be used when we need to horizontally scale the application. 

