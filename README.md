# Trice(ratops) project

The project is a working demo of Web application which allows monitoring HTTP requests, including headers, parameters and cookies, coming to certain URL generated
by the application.

First of all, you obtain a *SID* (a.k.a. session ID) generated from the index page, or you may simply open browser page */trice?sid=<ANY SID YOU WISH>* on the Web
server and see all incoming *POST* or *GET* requests coming to URL */hook?sid=<YOUR SID>*, i.e. for the same session ID.

The solution does not require but might also be implemented with *STOMP* protocol, using [Spring STOMP support](http://docs.spring.io/spring-integration/reference/html/stomp.html)
for back-end and by [Stomp over WebSocket](http://www.jmesnil.net/stomp-websocket/doc/) JavaScript library for front-end. This solution would require
[RabbitMQ STOMP adapter](https://www.rabbitmq.com/stomp.html) enabled.

You may also be interested in reading [this manual](https://spring.io/guides/gs/messaging-stomp-websocket/).

This project is hosted on [GitHub](https://github.com/), can be deployed locally, to Docker containers, or to [Heroku](https://www.heroku.com/).

## Building the project

To build the project run

```sh
$ gradle clean build
```

Gradle will compile sources, process resources, create *trice-<VERSION>.jar* file in *build/libs* directory, run unit, integration and mutation tests.

Generated *JAR* artifact has manifest entries like these:

    Manifest-Version: 1.0
    Implementation-Vendor: Alexander Burchak
    Implementation-Title: Trice
    Implementation-Version: 1.0.0-SNAPSHOT
    Main-Class: org.springframework.boot.loader.JarLauncher
    Spring-Boot-Classes: BOOT-INF/classes/
    Spring-Boot-Lib: BOOT-INF/lib/
    Spring-Boot-Version: 1.4.0.RELEASE
    Start-Class: TriceApplication

If you want to make *ZIP* distribution, run this command instead:

```sh
$ gradle clean zip
```

It will create *trice-<VERSION>.zip* file in *build/distributions* directory.

To generate *jacoco* test coverage reports, execute

```sh
$ gradle jacocoTestReport
```

then open in browser *build/jacoco/html/index.html* page.

## Deploying the project

### Deployment to local host

#### RabbitMQ

Go to [RabbitMQ Download page](https://www.rabbitmq.com/download.html), download distribution version *3.6.5* or later and install it.

Trice application does not need extra plugins to be enabled for RabbitMQ, but you may enable management tools:

```sh
$ rabbitmq-plugins enable rabbitmq_management
```

#### Trice

Unpackage *build/distributions/trice-<VERSION>.zip* archive to *$TRICE_HOME* directory.

The application should be run using *trice.sh* script located in *bin* directory:

```sh
$ $TRICE/bin/trice.sh
```

The application is available on local port HTTP *8081*.

### Deployment to Docker containers

To use this approach, you need to have Docker version *1.11.0* and Docker Compose version *1.7.0* or later.

In the project root directory, execute

```sh
$ docker-compose build
```

This will download needed Docker images, from [Docker Hub](https://hub.docker.com/), will create new images for RabbitMQ and Trice application.

Next, run the containers with

```sh
$ docker-compose up
```

The application is available on local port HTTP *8081*.

When containers are not needed anymore, execute

```sh
$ docker-compose down
```

### Deployment to Heroku

Register on [Heroku](https://www.heroku.com/), install CLI tools from [here](https://devcenter.heroku.com/articles/heroku-command-line), go to the project directory and type

```sh
$ heroku create
$ heroku config:set GRADLE_TASK="zip -x test -x itest"
$ heroku git:remote -a mysterious-fjord-92487
$ heroku addons:create cloudamqp:lemur
```

Here, *mysterious-fjord-92487* is a name given to the application by Heroku. Push your changes with *git*, to start deployment:

```sh
$ git push heroku master
```

Use following command to start working with the application:

```sh
heroku open
```

## Monitoring the application

### Watching log files

Trice application creates log file *trice.log* in *$TRICE/logs* directory. The file is rolled over on daily basis.

### Using management console

Trice application management tools are available under */mgmt* Web context: [http://localhost:8081/mgmt]().
