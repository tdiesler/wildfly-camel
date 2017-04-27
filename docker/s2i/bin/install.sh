#!/bin/bash

LAYERS_CONF=${JBOSS_HOME}/modules/layers.conf
XML_CONFIGURATION=${JBOSS_HOME}/standalone/configuration/standalone-openshift.xml

# Add WildFly-Camel extension configuration
sed -i '/<extension module="org.wildfly.extension.undertow"\/>/a         <extension module="org.wildfly.extension.camel"\/>' ${XML_CONFIGURATION}

# Add WildFly-Camel subsystem configuration
sed -i '/<subsystem xmlns="urn:jboss:domain:weld:4.0"\/>/a         <subsystem xmlns="urn:jboss:domain:camel:1.0"\/>' ${XML_CONFIGURATION}

# Add fuse layer configuration
LAYERS=$(cat ${LAYERS_CONF} | cut -f2 -d=)
echo "layers=fuse,${LAYERS}" > ${LAYERS_CONF}
