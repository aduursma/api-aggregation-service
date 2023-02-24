# FedEx Assessment - API Aggregation Service

## Getting started

All Maven plugins and dependencies are available from [Maven Central](https://search.maven.org/). Please have installed:

Required:
* JDK 14 (or later)
* Docker (minimum version **18.03**)

Optional:
* Maven (latest)

## Quick start

The application can be build and run by either using a locally pre-installed version of Maven or by using the Maven wrapper which comes with the application.

No actual installation of Maven is required, so the instructions below will assume using the Maven wrapper.

In case you would like to use a locally pre-installed version of Maven just replace any **mvnw** command with **mvn** .


1. Build the application
    ```
    mvnw -Dall-profiles clean install
    ```
2. Start the application
    ```
    mvnw -Pdocker docker:start
    ```
   The application will be ready for use after about 15 seconds and will be available at: http://localhost:8080


3. Open a browser and navigate to: http://localhost:8080/api-docs/api-guide.html
   \
   \
   The API Guide contains the following chapters:
    - **Chapter 1:** Assessment (_describes the assessment_)
    - **Chapter 2:** Implementation (_describes how to use the API_)
    - **Chapter 3:** Design decisions (_describes the design decisions made_)


4. Stop the application when you're done
    ```
    mvnw -Pdocker docker:stop
    ```
   
---
**NOTE**

Docker containers can access local services running on the host by connecting to `host.docker.internal`, but:
- it only works on Docker for Windows / Mac by default
- it only works on Docker version **18.03** and later
- it’s Docker specific so it doesn’t exist in CRI-O or ContainerD with Kubernetes

The Docker docs say:

The host has a changing IP address (or none if you have no network access). We recommend that you connect to the special DNS name `host.docker.internal` which resolves to the internal IP address used by the host. _This is for development purpose and will not work in a production environment outside of Docker Desktop for Windows / Mac_.

**Source:** https://docs.docker.com/docker-for-windows/networking/#use-cases-and-workarounds
\
\
**What does this mean?**

The application is configured to run on Docker for Windows.

If you're on Windows and you are using a Docker version prior to **18.03** you should update the `backend.services.host` property in the `application.properties` file to match the proper IP address. In most cases this will be **10.0.75.1**.

The same applies if you're on a different operating system, or if you run your Docker containers differently.

---

## Building the application

Maven is set up with a variety of profiles, each with their own specific purpose.

### unit-test

```
mvnw clean install
```

Will compile the code, run unit tests and then package and install the application.

This profile is active by default, so running unit tests is part of any build.

Also, unit test code coverage is calculated. The code coverage report can be found here: **target/site/jacoco/index.html**

### documentation-test

```
mvnw -Pdocumentation-test clean test
```

Will compile the code, run unit tests, run documentation tests and then generate the documentation using the generated snippets. The generated documentation can be found here: **target/generated-docs/api-guide.html**

The generated documentation will also be packaged with the application and is available at: http://localhost:8080/api-docs/api-guide.html

Although I consider the documentation tests to be integration tests, I have attached the Maven Failsafe plugin to the **test** phase. This is due to the fact that I want to package the generated documentation with the application and since the **integration-test** phase is executed after the **package** phase, the generated documentation will not be packaged with the application.

### mutation-test

```
mvnw -Pmutation-test clean test
```

Will compile the code, run unit tests, run mutation tests and then generate a report which can be found here: **target/pit-reports/index.html**

### integration-test

```
mvnw -Pintegration-test clean verify
```

Will compile the code, run unit tests, run integration tests and then generate a code coverage report which can be found here: **target/site/jacoco-it/index.html**

The code coverage of both unit and integration tests is merged to assess the overall code coverage. The merged code coverage report can be found here: **target/site/jacoco-merged/index.html**

### docker

```
mvnw -Pdocker clean install
```

Will compile the code, run unit tests, package and install the application and build a Docker image.

### Running all profiles at once

```
mvnw -Dall-profiles clean install
```

Although running different profiles for different purposes can be very useful during development, or from within a CI/CD pipeline, the command above is a convenient way to run all profiles at once without having to specify them all separately.

## Running the application

```
mvnw -Pdocker docker:start
```

Will run the API Aggregation Service and backend services.

```
mvnw -Pdocker docker:stop
```

Will stop running the API Aggregation Service and backend services.
