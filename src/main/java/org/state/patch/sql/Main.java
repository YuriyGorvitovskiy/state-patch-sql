package org.state.patch.sql;

import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.ServiceConfig;
import org.state.patch.sql.zzz.consumer.PatchConsumer;
import org.state.patch.sql.zzz.database.Database;
import org.state.patch.sql.zzz.processor.PatchProcessor;

public class Main {

    public static void main(String[] args) {
        System.out.println("I'm state-patch-sql");

        ServiceConfig config = Configurator.extract(System.getProperties(), "org.state.patch.sql", new ServiceConfig());

        System.out.println("Configuring Patch Consumer.");
        PatchConsumer consumer = PatchConsumer.createConsumer(config.patchtopic);

        System.out.println("Configuring Database.");
        Database database = Database.createDatabase(config.entity.database);

        System.out.println("Configuring Processor.");
        PatchProcessor processor = new PatchProcessor(database);
        processor.prepareDatabase();

        System.out.println("Start Patch Consuming and Processing.");
        consumer.run(processor);

        System.out.println("Execution Completted.");
    }
}
