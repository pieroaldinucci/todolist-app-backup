![example workflow](https://github.com/Predictabowl/todolist-app/actions/workflows/maven.yml/badge.svg)
![example workflow](https://github.com/Predictabowl/todolist-app/actions/workflows/maven-e2e.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/Predictabowl/todolist-app/badge.svg?branch=master)](https://coveralls.io/github/Predictabowl/todolist-app?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Predictabowl_todolist-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Predictabowl_todolist-app)

# Todolist

## Maven build

The application can be build with the command:

```shell
mvn clean verify
```

The executable jar will be found in `todolist-app/configuration-module/target`.

## Run Locally for manual testing

The application can be ran locally  with one of the following options.

### Full application from the IDE

You can run the application with an IDE capable of running Maven Spring Projects, in this case the main class is located in the "configuration-module".

To run the application in this fashion you'll need to launch the basic docker-compose file in the root directory, with the command `dcup`.

The application will be accessible with a browser at the port 8080.

The mails sent by the application will be received by the mail serve which will take care to create an account for every new mail address.

An email client will be accessible at the port 8000 to check those emails.

### Standalone docker

If the application was already packaged then is possible to build a docker image from the root folder with the command:

```shell
docker build -t todolist .
```

Once the image is built the full application, with both local mailserver and mailclient as per in the previous case, can be launched with

```shell
docker-compose -f docker-compose-full.yml up
```

### Running integration tests from the IDE

The integration tests are all inside the `configuration-module`, but they need a running database to work. The docker-compose inside the module folder can be used to run the database and then launch the tests from the IDE itself.
