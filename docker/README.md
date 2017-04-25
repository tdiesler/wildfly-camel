## EAP Docker Setup

Details about the EAP 7.1 docker image are described [here](https://mojo.redhat.com/docs/DOC-1133140).

    $ minishift delete
    $ minishift start --vm-driver=virtualbox --memory 12048 --cpus 3 --insecure-registry brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888 --insecure-registry 172.30.0.0/16
    $ eval $(minishift docker-env)
    $ docker pull brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888/jboss-eap-7-tech-preview/eap71:1.0-17
    $ docker tag brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888/jboss-eap-7-tech-preview/eap71:1.0-17 jboss-eap-7-tech-preview/eap71
    
    
