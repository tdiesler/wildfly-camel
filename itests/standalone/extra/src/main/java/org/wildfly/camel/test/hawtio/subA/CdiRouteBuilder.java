package org.wildfly.camel.test.hawtio.subA;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
@ContextName("cdi-context")
public class CdiRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start").to("log:foo");
    }
}
