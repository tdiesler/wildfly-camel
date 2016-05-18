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
package org.wildfly.camel.test.config;

import static org.wildfly.extension.camel.config.WildFlyCamelConfigPlugin.NS_CAMEL;
import static org.wildfly.extras.config.NamespaceConstants.NS_DOMAIN;
import static org.wildfly.extras.config.NamespaceConstants.NS_SECURITY;
import static org.wildfly.extras.config.NamespaceConstants.NS_WELD;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wildfly.extension.camel.config.WildFlyCamelConfigPlugin;
import org.wildfly.extras.config.ConfigContext;
import org.wildfly.extras.config.ConfigException;
import org.wildfly.extras.config.ConfigPlugin;
import org.wildfly.extras.config.ConfigSupport;
import org.wildfly.extras.config.NamespaceRegistry;

public class StandaloneConfigTest extends ConfigTestSupport {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testStandaloneConfig() throws Exception {
        URL resurl = StandaloneConfigTest.class.getResource("/baseline/standalone.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        NamespaceRegistry registry = new NamespaceRegistry();
        ConfigPlugin plugin = new WildFlyCamelConfigPlugin(registry);

        Namespace[] domain = registry.getNamespaces(NS_DOMAIN);
        Namespace[] security = registry.getNamespaces(NS_SECURITY);
        Namespace[] camel = registry.getNamespaces(NS_CAMEL);
        Namespace[] weld = registry.getNamespaces(NS_WELD);

        ConfigContext context = ConfigSupport.createContext(null, Paths.get(resurl.toURI()), doc);
        plugin.applyStandaloneConfigChange(context, true);

        // Verify extension
        assertElementWithAttributeValueNotNull(doc.getRootElement(), "extension", "module", "org.wildfly.extension.camel", domain);

        // Verify system-properties
        Element element = ConfigSupport.findChildElement(doc.getRootElement(), "system-properties", domain);
        Assert.assertNotNull("system-properties not null", element);
        assertElementWithAttributeValueNotNull(element, "property", "name", "hawtio.realm", domain);

        // Verify weld
        List<Element> profiles = ConfigSupport.findProfileElements(doc, domain);
        Assert.assertEquals("One profile", 1, profiles.size());
        assertElementNotNull(profiles.get(0), "subsystem", weld);

        // Verify camel
        assertElementNotNull(profiles.get(0), "subsystem", camel);

        // Verify hawtio-domain
        assertElementWithAttributeValueNotNull(doc.getRootElement(), "security-domain", "name", "hawtio-domain", security);

        // outputDocumentContent(doc)
    }

    @Test
    public void testUnsupportedNamespaceVersion() throws Exception {
        URL resurl = StandaloneConfigTest.class.getResource("/baseline/standalone.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        NamespaceRegistry fakeRegistry = new FakeNamespaceRegistry();

        ConfigContext context = ConfigSupport.createContext(null, Paths.get(resurl.toURI()), doc);
        ConfigPlugin plugin = new WildFlyCamelConfigPlugin(fakeRegistry);

        expectedException.expect(ConfigException.class);

        plugin.applyStandaloneConfigChange(context, true);
    }

    @Test
    public void testApplyConfigMultipleTimes() throws Exception {
        URL resurl = StandaloneConfigTest.class.getResource("/baseline/standalone.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        NamespaceRegistry registry = new NamespaceRegistry();
        ConfigPlugin plugin = new WildFlyCamelConfigPlugin(registry);

        Namespace[] camel = registry.getNamespaces(NS_CAMEL);
        ConfigContext context = ConfigSupport.createContext(null, Paths.get(resurl.toURI()), doc);

        for (int i = 0; i < 5; i++) {
            plugin.applyStandaloneConfigChange(context, true);
        }

        Assert.assertEquals(1, getElementCount(doc, "extension", null, new Attribute("module", "org.wildfly.extension.camel")));
        Assert.assertEquals(1, getElementCount(doc, "system-properties", null, null));
        Assert.assertEquals(1, getElementCount(doc, "security-domain", null, new Attribute("name", "hawtio-domain")));
        Assert.assertEquals(1, getElementCount(doc, "subsystem", camel[0], null));
    }

    @Test
    public void testRemoveConfig() throws Exception {
        URL resurl = StandaloneConfigTest.class.getResource("/modified/standalone-modified.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        NamespaceRegistry registry = new NamespaceRegistry();
        ConfigPlugin plugin = new WildFlyCamelConfigPlugin(registry);

        Namespace[] domain = registry.getNamespaces(NS_DOMAIN);
        Namespace[] security = registry.getNamespaces(NS_SECURITY);
        Namespace[] camel = registry.getNamespaces(NS_CAMEL);

        ConfigContext context = ConfigSupport.createContext(null, Paths.get(resurl.toURI()), doc);
        plugin.applyStandaloneConfigChange(context, false);

        // Verify extension removed
        assertElementWithAttributeValueNull(doc.getRootElement(), "extension", "module", "org.wildfly.extension.camel", domain);

        // Verify system-properties removed
        Element element = ConfigSupport.findChildElement(doc.getRootElement(), "system-properties", domain);
        Assert.assertNotNull("system-properties not null", element);
        assertElementWithAttributeValueNull(element, "property", "name", "hawtio.realm", domain);
        assertElementWithAttributeValueNull(element, "property", "name", "hawtio.offline", domain);
        assertElementWithAttributeValueNull(element, "property", "name", "hawtio.authenticationEnabled", domain);

        List<Element> profiles = ConfigSupport.findProfileElements(doc, domain);

        // Verify camel removed
        assertElementNull(profiles.get(0), "subsystem", camel);

        // Verify hawtio-domain removed
        assertElementWithAttributeValueNull(doc.getRootElement(), "security-domain", "name", "hawtio-domain", security);
    }

    private class FakeNamespaceRegistry extends NamespaceRegistry {
        @Override
        public void registerNamespace(String namespace, String version) {
            super.registerNamespace(NS_DOMAIN, "99.99");
        }
    }
}
