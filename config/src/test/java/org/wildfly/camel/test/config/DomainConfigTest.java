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

import static org.wildfly.extras.config.NamespaceConstants.NS_DOMAIN;

import java.net.URL;
import java.nio.file.Paths;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.extension.camel.config.WildFlyCamelConfigPlugin;
import org.wildfly.extras.config.ConfigContext;
import org.wildfly.extras.config.ConfigPlugin;
import org.wildfly.extras.config.ConfigSupport;
import org.wildfly.extras.config.NamespaceRegistry;

public class DomainConfigTest {

    @Test
    public void testDomainConfig() throws Exception {

        URL resurl = DomainConfigTest.class.getResource("/domain.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        NamespaceRegistry registry = new NamespaceRegistry();
        ConfigPlugin plugin = new WildFlyCamelConfigPlugin(registry);

        Namespace[] domainNamespaces = registry.getNamespaces(NS_DOMAIN);

        ConfigContext context = ConfigSupport.createContext(null, Paths.get(resurl.toURI()), doc);
        plugin.applyDomainConfigChange(context, true);

        Element element = ConfigSupport.findChildElement(doc.getRootElement(), "server-groups", domainNamespaces);
        Assert.assertNotNull("server-groups not null", element);
        element = ConfigSupport.findElementWithAttributeValue(element, "server-group", "name", "camel-server-group", domainNamespaces);
        Assert.assertNotNull("camel-server-group not null", element);

        XMLOutputter output = new XMLOutputter();
        output.setFormat(Format.getRawFormat());
        //System.out.println(output.outputString(doc));
    }
}
