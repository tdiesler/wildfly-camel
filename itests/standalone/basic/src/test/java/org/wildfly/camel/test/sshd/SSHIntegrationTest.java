/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2016 RedHat
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

package org.wildfly.camel.test.sshd;

import java.nio.file.Paths;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.test.common.ssh.EmbeddedSSHServer;
import org.wildfly.camel.test.common.utils.TestUtils;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
@RunWith(Arquillian.class)
@ServerSetup({SSHIntegrationTest.SSHServerSetupTask.class})
public class SSHIntegrationTest {

    static class SSHServerSetupTask implements ServerSetupTask {

        static final EmbeddedSSHServer sshServer = new EmbeddedSSHServer(Paths.get("target/sshd"));

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            sshServer.start();
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            sshServer.stop();
        }
    }

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "sshd-tests.jar")
            .addAsResource(new StringAsset(SSHServerSetupTask.sshServer.getConnection()), "ssh-connection")
            .addClasses(TestUtils.class);
    }

    @Test
    public void testSSHConsumer() throws Exception {

        String conUrl = TestUtils.getResourceValue(getClass(), "/ssh-connection");

        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                fromF("ssh://admin@%s?username=admin&password=admin&pollCommand=echo Hello Kermit", conUrl)
                .to("mock:end");
            }
        });

        camelctx.start();
        try {
            MockEndpoint mockEndpoint = camelctx.getEndpoint("mock:end", MockEndpoint.class);
            mockEndpoint.expectedMessageCount(1);
            mockEndpoint.setAssertPeriod(100);
            mockEndpoint.expectedBodiesReceived("Hello Kermit" + System.lineSeparator());
        } finally {
            camelctx.stop();
        }
    }

    @Test
    public void testSSHProducer() throws Exception {

        String conUrl = TestUtils.getResourceValue(getClass(), "/ssh-connection");

        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                .toF("ssh://admin@%s?username=admin&password=admin", conUrl);
            }
        });

        camelctx.start();
        try {
            ProducerTemplate template = camelctx.createProducerTemplate();
            String result = template.requestBody("direct:start", "echo Hello Kermit", String.class);
            Assert.assertEquals("Running command: echo Hello Kermit", result);
        } finally {
            camelctx.stop();
        }
    }
}
