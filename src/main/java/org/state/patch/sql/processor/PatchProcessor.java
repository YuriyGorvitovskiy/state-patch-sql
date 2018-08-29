package org.state.patch.sql.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

import org.state.patch.sql.database.Database;
import org.state.patch.sql.patch.Operation;
import org.state.patch.sql.patch.Patch;
import org.state.patch.sql.patch.Table;

public class PatchProcessor implements Consumer<Patch> {

    final Database database;

    public PatchProcessor(Database database) {
        this.database = database;
    }

    @Override
    public void accept(Patch patch) {
        for (Operation operation : patch.operations) {
            if (operation instanceof Table) {
                createTable((Table) operation);
            }
        }
    }

    private void createTable(Table operation) {
        try (Connection connection = database.datasource.getConnection()) {
            String sql = database.sqlCreateTable(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
