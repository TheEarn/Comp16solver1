package de.comp16.camelsolver1;

import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 * Main Camel app for Components 2016s Solver1
 * @author Felix Steinmeier
 * @author Carina Krämer
 */
public class MainApp {
	
	private Main main;

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
    	MainApp mainApp = new MainApp();
        mainApp.boot(args);
    }
    
    public void boot(String... args) throws Exception {
        // create a Main instance
        main = new Main();
        // add routes
        main.addRouteBuilder(new MyRouteBuilder());
        main.addMainListener(new Events());
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    public static class Events extends MainListenerSupport {
    	 
        @Override
        public void afterStart(MainSupport main) {
            System.out.println("MainApp with Camel is now started!");
            SudokuMessage registerMessage = MessageHandler.registerAtBroker(true);
            try {
				main.getCamelTemplate().sendBody("direct:out", registerMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
 
        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("MainApp with Camel is now being stopped!");
            SudokuMessage registerMessage = MessageHandler.registerAtBroker(false);
            try {
				main.getCamelTemplate().sendBody("direct:out", registerMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
}

