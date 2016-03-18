/*
 * #%L
 * Wildfly Camel :: Subsystem
 * %%
 * Copyright (C) 2013 - 2017 RedHat
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
package org.wildfly.extension.camel.deployment;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.jboss.classfilewriter.ClassFile;
import org.jboss.classfilewriter.ClassMethod;
import org.jboss.classfilewriter.code.CodeAttribute;

/**
 * CamelContextActivator starts a SpringCamelContext via a dynamically created class associated with the
 * current deployment ClassLoader.
 *
 * This ensures that SLF4J Logger instances created by Camel are associated with the correct
 * JBoss Logging LogProfile, when custom user log profiles are in operation.
 *
 * See:
 *
 * https://issues.jboss.org/browse/ENTESB-7117
 * https://github.com/wildfly-extras/wildfly-camel/issues/1919
 */
public class CamelContextActivator {

    private static final String CLASS_NAME = "org.wildfly.extension.camel.deployment.CamelContextActivatorProxy";

    public void activate(CamelContext camelctx, ClassLoader classLoader) throws Exception {
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(CLASS_NAME);
        } catch (ClassNotFoundException e) {
            ClassFile classFile = createContextActivatorClassFile();
            clazz = classFile.define(classLoader);
        }

        Object object = clazz.newInstance();
        Method method = object.getClass().getMethod("activate", SpringCamelContext.class);
        method.invoke(object, camelctx);
    }

    private ClassFile createContextActivatorClassFile() {
        ClassFile classFile = new ClassFile(CLASS_NAME, Object.class.getName());

        ClassMethod constructor = classFile.addMethod(Modifier.PUBLIC, "<init>", "V");
        constructor.getCodeAttribute().aload(0);
        constructor.getCodeAttribute().invokespecial(Object.class.getName(), "<init>", "()V");
        constructor.getCodeAttribute().returnInstruction();

        ClassMethod method = classFile.addMethod(Modifier.PUBLIC, "activate", "V", "Lorg/apache/camel/spring/SpringCamelContext;");
        CodeAttribute codeAttribute = method.getCodeAttribute();
        codeAttribute.aload(1);
        codeAttribute.invokevirtual("org/apache/camel/spring/SpringCamelContext", "start", "()V");
        codeAttribute.returnInstruction();

        return classFile;
    }
}
