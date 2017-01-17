/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.extension.camel.config;

import static org.wildfly.extras.config.NamespaceConstants.NS_DOMAIN;
import static org.wildfly.extras.config.NamespaceConstants.NS_LOGGING;
import static org.wildfly.extras.config.NamespaceConstants.NS_SECURITY;
import static org.wildfly.extras.config.NamespaceConstants.NS_WELD;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.wildfly.extras.config.ConfigContext;
import org.wildfly.extras.config.ConfigPlugin;
import org.wildfly.extras.config.ConfigSupport;
import org.wildfly.extras.config.LayerConfig;
import org.wildfly.extras.config.NamespaceRegistry;

public final class WildFlyCamelConfigPlugin implements ConfigPlugin {

    public static final String NS_CAMEL = "urn:jboss:domain:camel";

    private NamespaceRegistry registry;

    public WildFlyCamelConfigPlugin() {
        registerNamespaceVersions(new NamespaceRegistry());
    }

    public WildFlyCamelConfigPlugin(NamespaceRegistry registry) {
        registerNamespaceVersions(registry);
    }

    @Override
    public String getConfigName() {
        return "camel";
    }

    @Override
    public List<LayerConfig> getLayerConfigs() {
        return Arrays.asList(LayerConfig.FUSE_LAYER);
    }

    @Override
    public void applyStandaloneConfigChange(ConfigContext context, boolean enable) {
        updateExtension(context, enable);
        updateSystemProperties(context, enable);
        updateSubsystem(context, enable);
        updateSecurityDomain(context, enable);
    }

    @Override
    public void applyDomainConfigChange(ConfigContext context, boolean enable) {
        applyStandaloneConfigChange(context, enable);
        updateServergroup(enable, context);
    }

    private void updateExtension(ConfigContext context, boolean enable) {
        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);
        Element extensions = ConfigSupport.findChildElement(context.getDocument().getRootElement(), "extensions", domainNamespaces);
        ConfigSupport.assertExists(extensions, "Did not find the <extensions> element");
        Element element = ConfigSupport.findElementWithAttributeValue(extensions, "extension", "module", "org.wildfly.extension.camel", domainNamespaces);

        Namespace domainNamespace = context.getDocument().getRootElement().getNamespace();
        if (enable && element == null) {
            extensions.addContent(new Text("    "));
            extensions.addContent(new Element("extension", domainNamespace).setAttribute("module", "org.wildfly.extension.camel"));
            extensions.addContent(new Text("\n    "));
        }
        if (!enable && element != null) {
            element.getParentElement().removeContent(element);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateSystemProperties(ConfigContext context, boolean enable) {
        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);
        Namespace domainNamespace = context.getDocument().getRootElement().getNamespace();

        Element rootElement = context.getDocument().getRootElement();
        Element element = ConfigSupport.findChildElement(rootElement, "system-properties", domainNamespaces);
        if (element == null) {
            element = new Element("system-properties", domainNamespace);
            element.addContent(new Text("\n    "));

            int pos = rootElement.indexOf(rootElement.getChild("extensions", domainNamespace));
            rootElement.addContent(pos + 1, new Text("    "));
            rootElement.addContent(pos + 1, element);
            rootElement.addContent(pos + 1, new Text("\n"));
        }

        Map<String, Element> propertiesByName = ConfigSupport.mapByAttributeName(element.getChildren(), "name");
        if (enable) {
            addProperty(element, propertiesByName, "hawtio.authenticationEnabled", "true", domainNamespace);
            addProperty(element, propertiesByName, "hawtio.offline", "true", domainNamespace);
            addProperty(element, propertiesByName, "hawtio.realm", "hawtio-domain", domainNamespace);
            addProperty(element, propertiesByName, "hawtio.proxyWhitelist", "localhost, 127.0.0.1", domainNamespace);
        } else {
            removeProperty(propertiesByName, "hawtio.authenticationEnabled");
            removeProperty(propertiesByName, "hawtio.offline");
            removeProperty(propertiesByName, "hawtio.realm");
            removeProperty(propertiesByName, "hawtio.proxyWhitelist");
        }
    }

    private void updateSubsystem(ConfigContext context, boolean enable) {
        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);
        Namespace[] camelNamespaces = registry.getNamespaces(NS_CAMEL);

        List<Element> profiles = ConfigSupport.findProfileElements(context.getDocument(), domainNamespaces);
        for (Element profile : profiles) {
            Element element = ConfigSupport.findChildElement(profile, "subsystem", camelNamespaces);
            if (enable && element == null) {
                URL resource = WildFlyCamelConfigPlugin.class.getResource("/camel-subsystem.xml");
                profile.addContent(new Text("    "));
                profile.addContent(ConfigSupport.loadElementFrom(resource));
                profile.addContent(new Text("\n    "));
            }
            if (!enable && element != null) {
                element.getParentElement().removeContent(element);
            }
        }
    }

    private void updateSecurityDomain(ConfigContext context, boolean enable) {
        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);
        Namespace[] securityNamespaces = registry.getNamespaces(NS_SECURITY);

        List<Element> profiles = ConfigSupport.findProfileElements(context.getDocument(), domainNamespaces);
        for (Element profile : profiles) {
            Element security = ConfigSupport.findChildElement(profile, "subsystem", securityNamespaces);
            ConfigSupport.assertExists(security, "Did not find the security subsystem");
            Element domains = ConfigSupport.findChildElement(security, "security-domains", securityNamespaces);
            ConfigSupport.assertExists(domains, "Did not find the <security-domains> element");
            Element domain = ConfigSupport.findElementWithAttributeValue(domains, "security-domain", "name", "hawtio-domain", securityNamespaces);
            if (enable && domain == null) {
                URL resource = WildFlyCamelConfigPlugin.class.getResource("/hawtio-security-domain.xml");
                domains.addContent(new Text("    "));
                domains.addContent(ConfigSupport.loadElementFrom(resource));
                domains.addContent(new Text("\n            "));
            }
            if (!enable && domain != null) {
                domain.getParentElement().removeContent(domain);
            }
        }
    }

    private void updateServergroup(boolean enable, ConfigContext context) {
        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);

        Element serverGroups = ConfigSupport.findChildElement(context.getDocument().getRootElement(), "server-groups", domainNamespaces);
        Element camel = ConfigSupport.findElementWithAttributeValue(serverGroups, "server-group", "name", "camel-server-group", domainNamespaces);
        if (enable && camel == null) {
            URL resource = WildFlyCamelConfigPlugin.class.getResource("/camel-servergroup.xml");
            Element serverGroup = ConfigSupport.loadElementFrom(resource);
            serverGroups.addContent(new Text("    "));
            serverGroups.addContent(serverGroup);
            serverGroups.addContent(new Text("\n    "));
            applyNamespace(serverGroup);
        }
        if (!enable && camel != null) {
            camel.getParentElement().removeContent(camel);
        }
    }

    private void addProperty(Element systemProperties, Map<String, Element> propertiesByName, String name, String value, Namespace namespace) {
        if (!propertiesByName.containsKey(name)) {
            systemProperties.addContent(new Text("   "));
            systemProperties.addContent(new Element("property", namespace).setAttribute("name", name).setAttribute("value", value));
            systemProperties.addContent(new Text("\n    "));
        }
    }

    private void removeProperty(Map<String, Element> propertiesByName, String name) {
        Element element = propertiesByName.get(name);
        if (element != null) {
            element.getParentElement().removeContent(element);
        }
    }

    private void registerNamespaceVersions(NamespaceRegistry registry) {
        this.registry = registry;
        registry.registerNamespace(NS_DOMAIN, "1.8");
        registry.registerNamespace(NS_DOMAIN, "1.7");
        registry.registerNamespace(NS_CAMEL, "1.0");
        registry.registerNamespace(NS_LOGGING, "1.5");
        registry.registerNamespace(NS_SECURITY, "1.2");
        registry.registerNamespace(NS_WELD, "1.0");
    }

    private void applyNamespace(Element element) {
        if (element.getParentElement() != null) {
            element.setNamespace(element.getParentElement().getNamespace());
        }
        for (Element child : (List<Element>) element.getChildren()) {
            applyNamespace(child);
        }
    }
}
