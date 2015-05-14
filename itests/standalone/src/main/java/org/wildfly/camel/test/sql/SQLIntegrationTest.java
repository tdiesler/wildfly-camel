/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2014 RedHat
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

package org.wildfly.camel.test.sql;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.PollingConsumer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SQLIntegrationTest {

    @Resource(mappedName = "java:jboss/datasources/ExampleDS")
    DataSource dataSource;

    @Deployment
    public static JavaArchive createdeployment() throws IOException {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "camel-sql-tests.jar");
        archive.addAsManifestResource(new StringAsset(""), "beans.xml");
        return archive;
    }

    @Test
    public void testSQLEndpoint() throws Exception {

        Assert.assertNotNull("DataSource not null", dataSource);

        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("sql:select name from information_schema.users?dataSource=java:jboss/datasources/ExampleDS")
                .to("direct:end");
            }
        });

        camelctx.start();
        try {
            PollingConsumer pollingConsumer = camelctx.getEndpoint("direct:end").createPollingConsumer();
            pollingConsumer.start();

            String result = (String) pollingConsumer.receive().getIn().getBody(Map.class).get("NAME");
            Assert.assertEquals("SA", result);
        } finally {
            camelctx.stop();
        }
    }
}
