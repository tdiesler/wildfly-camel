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

package org.wildfly.camel.test.hawtio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.test.common.http.HttpRequest;
import org.wildfly.camel.test.hawtio.subA.CdiRouteBuilder;

@RunWith(Arquillian.class)
public class HawtIOIntegrationTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "mgmnt-pa$$wrd1";
    private static final String CAMEL_TEST_JAR = "camel-tests.jar";

    @ArquillianResource
    private Deployer deployer;

    @Deployment
    public static JavaArchive deployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "hawtio-tests");
        archive.addClass(HttpRequest.class);
        return archive;
    }

    @Deployment(testable = false, managed = false, name = CAMEL_TEST_JAR)
    public static JavaArchive otherDeployment() {
        return ShrinkWrap.create(JavaArchive.class, CAMEL_TEST_JAR)
            .addClass(CdiRouteBuilder.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testAccessHawtIO() throws Exception {

        URL url = new URL("http://localhost:8080/hawtio/jolokia/read/java.lang:type=Memory/HeapMemoryUsage/used");
        URLConnection conn = url.openConnection();
        try {
            // Accessing hawtio requires an admin id/password.
            String userpass = USERNAME + ":" + PASSWORD;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            int code = ((HttpURLConnection) conn).getResponseCode();
            assertEquals(200, code);
            String content = new String(readFully((InputStream) conn.getContent()), "UTF-8");

            // Let check to see if it has some of the expected output.
            assertTrue("Unexpected content: "+content, content.contains("\"mbean\":\"java.lang:type=Memory\""));

        } finally {
            try {
                conn.getInputStream().close();
            } catch (Throwable ignore) {
            }
        }

    }

    @Test
    public void testUnauthedAccessHawtIO() throws Exception {

        URL url = new URL("http://localhost:8080/hawtio/jolokia/read/java.lang:type=Memory/HeapMemoryUsage/used");
        URLConnection conn = url.openConnection();
        try {
            // Accessing hawtio requires an admin id/password since none is
            // given in this case, we should get a 403 error code.
            int code = ((HttpURLConnection) conn).getResponseCode();
            assertEquals(403, code);
        } finally {
            try {
                conn.getInputStream().close();
            } catch (Throwable ignore) {
            }
        }

    }

    @Test
    public void testHawtioGetCamelContextComponents() throws Exception {
        String userpass = USERNAME + ":" + PASSWORD;
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

        deployer.deploy(CAMEL_TEST_JAR);
        try {
            HttpRequest.HttpResponse response = HttpRequest
                .post("http://localhost:8080/hawtio/jolokia/?maxDepth=7&maxCollectionSize=5000&ignoreErrors=true&canonicalNaming=false")
                .header("Authorization", basicAuth)
                .content("{\"type\":\"exec\",\"mbean\":\"org.apache.camel:context=cdi-context,type=context,name=\\\"cdi-context\\\"\",\"operation\":\"findComponentNames\"}")
                .getResponse();

//            System.out.println("===========================================================");
//            System.out.println(response.getBody());
//            System.out.println("===========================================================");

            Assert.assertTrue(response.getBody().contains("\"status\":200"));
        } finally {
            deployer.undeploy(CAMEL_TEST_JAR);
        }
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }
}
