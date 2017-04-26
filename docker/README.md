## EAP Docker Setup

Details about the EAP 7.1 docker image are described [here](https://mojo.redhat.com/docs/DOC-1133140).

    $ minishift delete
    $ minishift start --vm-driver=virtualbox --memory 12048 --cpus 3 --insecure-registry brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888 --insecure-registry 172.30.0.0/16
    $ eval $(minishift docker-env)
    
Build the EAP Camel docker image

    $ mvn clean install -Ddocker -pl docker -am

Run the EAP Camel docker image

    $ docker run --rm -ti -e WILDFLY_MANAGEMENT_USER=admin -e WILDFLY_MANAGEMENT_PASSWORD=admin -p 8080:8080 -p 9990:9990 jboss-fuse/eap71-camel
    
Test access to the console http://192.168.99.100:9990/console and to hawtio http://192.168.99.100:8080/hawtio


