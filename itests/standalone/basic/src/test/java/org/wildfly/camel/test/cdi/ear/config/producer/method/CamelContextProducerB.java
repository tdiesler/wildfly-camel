/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2018 RedHat
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
package org.wildfly.camel.test.cdi.ear.config.producer.method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
public class CamelContextProducerB {

    @Inject
    CamelContext camelctx;

    @Produces
    @ApplicationScoped
    CamelContext customize() {
        DefaultCamelContext camelctx = new DefaultCamelContext();
        camelctx.setName("context-producer-b");
        return camelctx;
    }
}
