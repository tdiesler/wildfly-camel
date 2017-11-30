FROM ${docker.image.from}

LABEL io.openshift.tags="builder,javaee,eap,eap7" \
      io.k8s.description="Platform for building and running Apache Camel applications on EAP 7.1" \
      io.k8s.display-name="Fuse Integration Services - EAP"

##########################################################
# NOTE: The following commands work around CLOUD-1945
#
# https://issues.jboss.org/browse/CLOUD-1945
#
# Ideally we'd run fuse-patch or the fuse-eap-installer.jar
#
##########################################################

# Add & extract fuse-eap distribution
COPY wildfly-camel-patch.tar.gz /tmp/wildfly-camel-patch.tar.gz
RUN tar xvzf /tmp/wildfly-camel-patch.tar.gz -C ${JBOSS_HOME} --exclude "bin" --exclude "fusepatch" --exclude "quickstarts" --exclude "modules/layers.conf" --exclude "standalone"

user root
RUN rm -f /tmp/wildfly-camel-patch.tar.gz
user 185

# Add WildFly-Camel extension configuration
RUN sed -i '/<extension module="org.wildfly.extension.undertow"\/>/ a\        <extension module="org.wildfly.extension.camel"\/>' ${JBOSS_HOME}/standalone/configuration/standalone-openshift.xml

# Add WildFly-Camel subsystem configuration
RUN sed -i '/<subsystem xmlns="urn:jboss:domain:weld:.*"\/>/ a\        <subsystem xmlns="urn:jboss:domain:camel:1.0"\/>' ${JBOSS_HOME}/standalone/configuration/standalone-openshift.xml

# Add fuse layer configuration
RUN LAYERS=$(cat ${JBOSS_HOME}/modules/layers.conf | cut -f2 -d=); echo "layers=fuse,${LAYERS}" > ${JBOSS_HOME}/modules/layers.conf
