## WildFly Camel

Provides [Apache Camel](http://camel.apache.org/) integration with the [WildFly Application Server](http://wildfly.org/).

The WildFly-Camel Subsystem allows you to add Camel Routes as part of the EAP configuration. Routes can be deployed as part of JavaEE applications. JavaEE components can access the Camel Core API and various Camel Component APIs.

Your Enterprise Integration Solution can be architected as a combination of JavaEE and Camel functionality.

### Documentation

* [JBoss Fuse Documentation](https://access.redhat.com/documentation/en/red-hat-jboss-fuse/)

### System Requirements

#### Java

Minimum of Java 1.7, to run EAP and Maven.

#### Maven

Minimum of Maven 3.2.3.

### Build

The default build is straight forward

```
$ mvn clean install
```

The extended build includes the set of JavaEE integration examples

```
$ mvn clean install -Dts.all
```
### Install

Refer to the 'Installation on JBoss EAP' section within the [JBoss Fuse Documentation](https://access.redhat.com/documentation/en/red-hat-jboss-fuse/).

### Run

In your EAP home directory run ...

```
$ bin/standalone.sh -c standalone.xml
```

Access EAP Management Console at http://192.168.59.103:9990 and the Hawtio console at http://192.168.59.103:8080/hawtio
