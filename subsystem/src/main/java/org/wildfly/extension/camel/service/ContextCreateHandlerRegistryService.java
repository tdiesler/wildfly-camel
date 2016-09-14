/*
 * #%L
 * Wildfly Camel :: Subsystem
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

package org.wildfly.extension.camel.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.gravia.runtime.ModuleContext;
import org.jboss.gravia.runtime.Runtime;
import org.jboss.gravia.runtime.ServiceRegistration;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.extension.camel.CamelConstants;
import org.wildfly.extension.camel.ContextCreateHandler;
import org.wildfly.extension.camel.ContextCreateHandlerRegistry;
import org.wildfly.extension.camel.handler.ClassResolverAssociationHandler;
import org.wildfly.extension.camel.handler.ContextValidationHandler;
import org.wildfly.extension.camel.handler.ModelJAXBContextFactoryWrapperHandler;
import org.wildfly.extension.camel.handler.ModuleClassLoaderAssociationHandler;
import org.wildfly.extension.camel.handler.NamingContextAssociationHandler;
import org.wildfly.extension.gravia.GraviaConstants;

/**
 * The {@link ContextCreateHandlerRegistry} service
 *
 * @author Thomas.Diesler@jboss.com
 * @since 13-Mar-2015
 */
public class ContextCreateHandlerRegistryService extends AbstractService<ContextCreateHandlerRegistry> {

    private final InjectedValue<Runtime> injectedRuntime = new InjectedValue<>();

    private ServiceRegistration<ContextCreateHandlerRegistry> registration;
    private ContextCreateHandlerRegistry createHandlerRegistry;

    public static ServiceController<ContextCreateHandlerRegistry> addService(ServiceTarget serviceTarget, ServiceVerificationHandler verificationHandler) {
        ContextCreateHandlerRegistryService service = new ContextCreateHandlerRegistryService();
        ServiceBuilder<ContextCreateHandlerRegistry> builder = serviceTarget.addService(CamelConstants.CONTEXT_CREATE_HANDLER_REGISTRY_SERVICE_NAME, service);
        builder.addDependency(GraviaConstants.RUNTIME_SERVICE_NAME, Runtime.class, service.injectedRuntime);
        builder.addListener(verificationHandler);
        return builder.install();
    }

    @Override
    public void start(StartContext startContext) throws StartException {
        ServiceRegistry serviceRegistry = startContext.getController().getServiceContainer();
        createHandlerRegistry = new ContextCreateHandlerRegistryImpl(serviceRegistry, startContext.getChildTarget());

        Runtime runtime = injectedRuntime.getValue();
        ModuleContext syscontext = runtime.getModuleContext();
        registration = syscontext.registerService(ContextCreateHandlerRegistry.class, createHandlerRegistry, null);
    }

    @Override
    public void stop(StopContext context) {
        if (registration != null) {
            registration.unregister();
        }
    }

    @Override
    public ContextCreateHandlerRegistry getValue() {
        return createHandlerRegistry;
    }

    static final class ContextCreateHandlerRegistryImpl implements ContextCreateHandlerRegistry {

        private final Map<ClassLoader, List<ContextCreateHandler>> handlerMapping = new HashMap<>();

        ContextCreateHandlerRegistryImpl(final ServiceRegistry serviceRegistry, final ServiceTarget serviceTarget) {

            // Setup the default handlers
            addContextCreateHandler(null, new ModuleClassLoaderAssociationHandler());
            addContextCreateHandler(null, new ClassResolverAssociationHandler());
            addContextCreateHandler(null, new NamingContextAssociationHandler(serviceRegistry, serviceTarget));
            addContextCreateHandler(null, new ContextValidationHandler());
            addContextCreateHandler(null, new ModelJAXBContextFactoryWrapperHandler());
        }

        @Override
        public List<ContextCreateHandler> getContextCreateHandlers(ClassLoader classsLoader) {
            List<ContextCreateHandler> result = new ArrayList<>();
            synchronized (handlerMapping) {
                List<ContextCreateHandler> handlers = handlerMapping.get(classsLoader);
                if (handlers != null) {
                    result.addAll(handlers);
                }
            }
            return Collections.unmodifiableList(result);
        }

        @Override
        public void addContextCreateHandler(ClassLoader classsLoader, ContextCreateHandler handler) {
            synchronized (handlerMapping) {
                List<ContextCreateHandler> handlers = handlerMapping.get(classsLoader);
                if (handlers == null) {
                    handlers = new ArrayList<>();
                    handlerMapping.put(classsLoader, handlers);
                }
                handlers.add(handler);
            }
        }

        @Override
        public void removeContextCreateHandler(ClassLoader classsLoader, ContextCreateHandler handler) {
            synchronized (handlerMapping) {
                List<ContextCreateHandler> handlers = handlerMapping.get(classsLoader);
                if (handlers != null) {
                    handlers.remove(handler);
                    if (handlers.isEmpty()) {
                        handlerMapping.remove(classsLoader);
                    }
                }
            }
        }

        @Override
        public void removeContextCreateHandlers(ClassLoader classsLoader) {
            synchronized (handlerMapping) {
                handlerMapping.remove(classsLoader);
            }
        }

        @Override
        public boolean containsKey(ClassLoader classLoader) {
            synchronized (handlerMapping) {
                return handlerMapping.containsKey(classLoader);
            }
        }
    }
}
