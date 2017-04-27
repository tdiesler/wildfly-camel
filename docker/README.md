## EAP Docker Setup

Details about the EAP 7.1 docker image are described [here](https://mojo.redhat.com/docs/DOC-1133140).

    minishift delete
    minishift start --vm-driver=virtualbox --memory 12048 --cpus 3 --insecure-registry brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888 --insecure-registry 172.30.0.0/16
    eval $(minishift docker-env)

Build the EAP Camel docker image

    mvn clean install -Ddocker -pl docker -am

Run the EAP Camel docker image

    docker run --rm -ti -p 8080:8080 jboss-fuse/eap71-camel

