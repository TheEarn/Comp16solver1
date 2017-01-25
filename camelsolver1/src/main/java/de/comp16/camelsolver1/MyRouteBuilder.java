package de.comp16.camelsolver1;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)
        // then performs content based routing on the message using XPath
        from("file:src/data?noop=true")
        	.log("Got file: ${body}")
        	.unmarshal().json(JsonLibrary.Jackson, SudokuMessage.class)
        	.to("direct:handle");
        
        restConfiguration().component("undertow")
	        // use json binding mode so Camel automatic binds json <--> pojo
	        .bindingMode(RestBindingMode.json)
	        // set jackson properties
	        .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
	        // and output using pretty print
	        .dataFormatProperty("prettyPrint", "true")
	        // setup context path on localhost and port number that undertow will use
	        .contextPath("/").host("localhost").port(8080);
        
        rest("/rest_api")
	        .post("/solve").consumes("application/json").type(SudokuMessage.class).to("direct:handle");
	
	    from("direct:handle")
	    	.log("Got ${body}")
	    	.bean(MessageHandler.class, "postMessage");
//	    	.to("bean:messageHandler?method=postMessage");

    }

}
