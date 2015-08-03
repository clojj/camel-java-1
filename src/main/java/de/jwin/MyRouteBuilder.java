package de.jwin;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.processor.interceptor.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    Logger logger = LoggerFactory.getLogger(MyRouteBuilder.class);

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        logger.debug("configure routes...");

//        configureTracer();

        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)

        PropertiesComponent pc = getContext().getComponent("properties", PropertiesComponent.class);
        pc.setLocation("classpath:application.properties");

        from("stream:file?fileName=src/data/input.txt&scanStream=true&scanStreamDelay=1000")
//        from("file:src/data?noop=true&fileName=input.txt&delay=1000&idempotentKey=${file:name}-${file:modified}")
                .bean(PojoCreator.class)
                .split(body()).parallelProcessing()
                .split().method(PojoSplitter.class, "splitBody").parallelProcessing()
                .to("{{my.output}}");

        from("direct:my-output")
                .log("${body}");
/*
            .choice()
                .when(xpath("/person/city = 'London'"))
                    .log("UK message")
                    .to("file:target/messages/uk")
                .otherwise()
                    .log("Other message")
                    .to("file:target/messages/others");
*/
    }

    private void configureTracer() {
        Tracer tracer = new Tracer();
        tracer.getDefaultTraceFormatter().setShowExchangeId(true);
        tracer.getDefaultTraceFormatter().setShowBodyType(true);
        tracer.getDefaultTraceFormatter().setShowBreadCrumb(false);
        tracer.getDefaultTraceFormatter().setShowNode(false);
        getContext().addInterceptStrategy(tracer);
    }

}
