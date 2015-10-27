/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2015 RedHat
 * %%
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
 * #L%
 */

package org.wildfly.camel.test.spring;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelContextRegistry;

@RunWith(Arquillian.class)
public class SpringJdbcNamespaceTest {

    @ArquillianResource
    CamelContextRegistry contextRegistry;

    @Deployment
    public static JavaArchive createDeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "camel-jdbc-tests");
        archive.addAsResource("spring/jdbc-namespace-camel-context.xml");
        archive.addAsResource("spring/sql/db-schema.sql", "db-schema.sql");
        archive.addAsResource("spring/sql/db-data.sql", "db-data.sql");
        return archive;
    }

    @Test
    public void testSpringJdbcNamespace() throws Exception {
        CamelContext camelctx = contextRegistry.getCamelContext("spring-jdbc");
        Assert.assertNotNull(camelctx);

        MockEndpoint resultEndpoint = camelctx.getEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);

        MockEndpoint.assertWait(5, TimeUnit.SECONDS, resultEndpoint);
        resultEndpoint.assertIsSatisfied();

        String result = resultEndpoint.getExchanges().get(0).getIn().getBody(String.class);

        Assert.assertEquals("Hello kermit", result);
    }
}
