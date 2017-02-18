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

	public static final String OWN_HOST = "localhost"; //136.199.51.110
	public static final String OWN_PORT = "8080";	
	public static final String OWN_URI = "restlet:http://"+OWN_HOST+":"+OWN_PORT+"/rest_api/solve?restletMethod=post";
	public static final String BROKER_URI = "rabbitmq://136.199.51.111/inExchange?username=kompo&password=kompo&skipQueueDeclare=true&exchangeType=fanout&autoDelete=false";

    /**
     * Camel routes for Components 2016s Solver1
     * TODO: test receiving & implement sending via rest
     */
    public void configure() {

	getContext().setTracing(true);

        // Reading files from src/data, binding JSON to SudokuMessage-POJO
        from("file:src/data?noop=true")
        	.log("Got file: ${body}")
        	.unmarshal().json(JsonLibrary.Jackson, SudokuMessage.class)
        	.to("direct:handle");
        
        
        restConfiguration().component("restlet")
	        // use json binding mode so Camel automatic binds json <--> pojo
	        .bindingMode(RestBindingMode.json)
	        // set jackson properties
	        .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
	        // and output using pretty print
	        .dataFormatProperty("prettyPrint", "true")
	        // setup context path on localhost and port number that undertow will use
	        .contextPath("/").host(OWN_HOST).port(OWN_PORT);
        
        // Receiving messages via REST
        rest("/rest_api")
	        .post("/solve").consumes("application/json")
		.to("file:var/in_messages")
		.type(SudokuMessage.class).to("direct:handle");

        // Passing SudokuMessage to new MessageHandler
	    from("direct:handle")
	    	.log("Got ${body}")
	    	.bean(MessageHandler.class, "postMessage");
//	    	.to("bean:messageHandler?method=postMessage");
	    
	    from("direct:out")
	    	.marshal().json(JsonLibrary.Jackson)
	    	.to("file:var/out_messages")
//	    	.to(BROKER_URI) // !UNCOMMENT THIS FOR ACTUAL SENDING! TODO: Timeout-Handling
	    	;

    }

}
