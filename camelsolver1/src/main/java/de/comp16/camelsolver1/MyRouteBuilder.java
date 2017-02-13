package de.comp16.camelsolver1;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * RouteBuilder specifying Camel routes for Components 2016s Solver1
 * @see MessageHandler
 * @author Felix Steinmeier
 * @author Carina Krämer
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Camel routes for Components 2016s Solver1
     * TODO: test receiving & implement sending via rest
     */
    public void configure() {

        // Reading files from src/data, binding JSON to SudokuMessage-POJO
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
        
        // Receiving messages via REST
        rest("/rest_api")
	        .post("/solve").consumes("application/json").type(SudokuMessage.class).to("direct:handle");

        // Passing SudokuMessage to new MessageHandler
	    from("direct:handle")
	    	.log("Got ${body}")
	    	.bean(MessageHandler.class, "postMessage");
//	    	.to("bean:messageHandler?method=postMessage");
	    
	    from("direct:out")
	    	.marshal().json(JsonLibrary.Jackson)
	    	.to("file:var/out_messages")
//	    	.to(MessageHandler.BROKER_URI) // !UNCOMMENT THIS FOR ACTUAL SENDING! TODO: Timeout-Handling
	    	;

    }

}
