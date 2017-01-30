package de.comp16.camelsolver1;

import org.apache.camel.main.Main;

/**
 * Main Camel app for Components 2016s Solver1
 * @author Felix Steinmeier
 * @author Carina Krämer
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.addRouteBuilder(new MyRouteBuilder());
        main.run(args);
    }

}

