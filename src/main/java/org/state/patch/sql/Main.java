package org.state.patch.sql;

import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.ServiceConfig;

public class Main {

    public static void main(String[] args) {
        System.out.println("I'm state-patch-sql");

        ServiceConfig config = Configurator.extract(System.getProperties(), "org.state.patch.sql", new ServiceConfig());
        System.out.println("My name is: " + config.name);

        System.out.println("Configuring Patch Processor.");
        PatchProcessor processor = new PatchProcessor(config);
        processor.run();

        System.out.println("Execution Completted.");
    }
}
