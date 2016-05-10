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
package org.wildfly.camel.test.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.test.common.http.HttpRequest;
import org.wildfly.camel.test.rest.subA.Response;
import org.wildfly.extension.camel.CamelAware;

/**
 * Relates to customer case 01608161 / ENTESB-5282
 */
@CamelAware
@RunWith(Arquillian.class)
public class RestDslJacksonMarshalIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "rest-jackson-tests.war");
        archive.addClass(HttpRequest.class);
        archive.addPackage(Response.class.getPackage());
        archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        archive.addAsWebInfResource("rest/web.xml", "web.xml");
        return archive;
    }

    @Test
    public void testJacksonUnmarshalEndpoint() throws Exception {
        HttpRequest.HttpResponse response = HttpRequest.get("http://localhost:8080/rest-jackson-tests/rest/foo").getResponse();
        Assert.assertEquals("Expected HTTP status code to be 200", 200, response.getStatusCode());
    }
}
