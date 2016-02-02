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

package org.wildfly.camel.test.dozer;

import javax.el.ExpressionFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelAware;


@CamelAware
@RunWith(Arquillian.class)
public class ExpressionFactoryTest {

    @Deployment
    public static JavaArchive createdeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "el-factory-tests");
        return archive;
    }

    @Test
    public void testStatelessSessionBean() throws Exception {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            ExpressionFactory factory = ExpressionFactory.newInstance();
            Assert.assertEquals("org.apache.el.ExpressionFactoryImpl", factory.getClass().getName());
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }
}
