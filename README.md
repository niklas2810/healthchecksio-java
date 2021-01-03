![healthchecksio-java](https://socialify.git.ci/niklas2810/healthchecksio-java/image?description=1&font=Source%20Code%20Pro&language=1&owner=1&pattern=Floating%20Cogs&theme=Dark)

<p align="center">
<a href="https://search.maven.org/artifact/com.niklasarndt/healthchecksio-java">
<img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.niklasarndt/healthchecksio-java?logo=java&style=for-the-badge">
</a>
<a href="https://github.com/niklas2810/healthchecksio-java/actions">
<img alt="GitHub Build" src="https://img.shields.io/github/workflow/status/niklas2810/healthchecksio-java/Build%20Project?logo=github&style=for-the-badge">
</a>
<a href="https://niklas2810.github.io/healthchecksio-java/apidocs/">
<img alt="Javadoc Link" src="https://img.shields.io/badge/Javadocs-Link-yellow?style=for-the-badge">
</a>
</p>

`healthchecksio-java` is a Java Library for the REST API of [healthchecks.io](https://healthchecks.io/) (it supports private instances as well). 
For now, only client features (Pinging API) are supported. 
However, the Management API is on my ToDo-List!

You can find more information on how to set up and use this library down below.

<hr>

**Table of Contents:**

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Advanced Usage](#advanced-usage)
- [Contributing](#contributing)
- [Dependencies](#dependencies)
- [License](#license)

## Installation

`healthchecksio-java` is available on [Maven Central](https://search.maven.org/artifact/com.niklasarndt/healthchecksio-java). You can find alternative importing methods (as well as direct download options) there.

The Javadocs of this project are available [here](https://niklas2810.github.io/healthchecksio-java/apidocs/).

Maven:
```xml
<dependency>
  <groupId>com.niklasarndt</groupId>
  <artifactId>healthchecksio-java</artifactId>
  <version>1.0.1</version>
</dependency>
```

Gradle:
```gradle
implementation 'com.niklasarndt:healthchecksio-java:1.0.0'
```

## Quick Start

Creating a Healthchecks client is quite straightforward.

```java
//Creates a new healthchecks.io pinging client for the specified UUID
HealthchecksClient client = Healthchecks.forUuid("<uuid>"); 
```

Use these commands to send events to healthchecks.io:

```java
client.start(); //The job has just begun
client.success(); //The job was completed successfully
client.fail(); //The job failed unexpectedly
client.exitCode(5); //The job exited with a specific exit code. Non-Zero exit codes will trigger alerts.
```

Every method also supports an attached message:

```java
client.fail("Could not execute the job: " + reason); //Sends this string to healthchecks.io, which will be visible on the dashboard
```

The same applies to the Manager:

```java
HealthchecksManager manager = Healthchecks.manager("<api-key>");
```

Read more about the [Management API](https://github.com/niklas2810/healthchecksio-java/wiki/Using-the-Manager)
in the wiki for further instructions, here's a quick example:

```java
Check[] filteredChecks = manager.getExistingChecks("cron debian").get(); //Retrieve all checks with tags "cron" and "debian"
```

To find out more about using the API, 
[visit the wiki](https://github.com/niklas2810/healthchecksio-java/wiki)!

This library has a `slf4-api`(https://mvnrepository.com/artifact/org.slf4j/slf4j-api) implementation which produces debug logging output. To prevent this from happening, add this line to your `logback.xml`:

```xml
<logger name="com.niklasarndt.healthchecksio" level="WARN"/>
```

From now on, only warnings will be added to your logs.


## Advanced Usage

You can also use this library with your self-hosted instance. To do this, simply use a different constructor function!

```java
HealthchecksClient client = Healthchecks.forUuid("https://healthchecks.example.com", "<uuid>"); 
```

The same scheme applies to the manager ([wiki](https://github.com/niklas2810/healthchecksio-java/wiki/Using-the-Manager#create-a-manager-object)).

## Contributing

If you have any questions regarding the project or spotted a bug, feel free to open an issue!

Pull requests are always very much appreciated, but I hope you'll forgive me if I'm slow to respond sometimes.

## Dependencies

- [OkHttp3](https://github.com/square/okhttp)
- [slf4j-api](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
- [jackson-databind](https://github.com/FasterXML/jackson)
- [lombok](https://projectlombok.org/)

Unit Testing:

- [junit5-jupiter](https://github.com/junit-team/junit5)
- [slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)

## License

&copy; Niklas Arndt 2020, [MIT License](LICENSE.md)

I do not own or claim any copyright of healthchecks.io. 
healthchecks.io  is being created by Pēteris Caune and other contributors
under the [BSD 3-Clause Revised License](https://github.com/healthchecks/healthchecks/blob/master/LICENSE).