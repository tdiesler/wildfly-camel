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
package org.wildfly.extension.camel.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

/**
 * A utility class to run arbitrary code via a {@link Proxy} instance.
 */
public class ProxyUtils {

    private ProxyUtils() {
        // Hide ctor
    }

    /**
     * Runs a {@link Callable} within a {@link Proxy} instance. See the following issues for information
     * around its primary use case.
     *
     * https://issues.jboss.org/browse/ENTESB-7117
     * https://github.com/wildfly-extras/wildfly-camel/issues/1919
     *
     * @param callable A callable instance to invoke within a {@link Proxy} instance
     * @param classLoader The ClassLoader used to create the {@link Proxy} instance
     * @throws Exception
     */
    public static void invokeProxied(final Callable<?> callable, final ClassLoader classLoader) throws Exception {
        Callable<?> callableProxy = (Callable) Proxy.newProxyInstance(classLoader, new Class<?>[] { Callable.class }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                callable.call();
                return null;
            }
        });
        callableProxy.call();
    }
}
