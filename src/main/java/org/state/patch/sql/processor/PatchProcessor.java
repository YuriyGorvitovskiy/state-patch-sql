package org.state.patch.sql.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

import org.state.patch.sql.database.Database;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteTable;
import org.state.patch.sql.patch.Operation;
import org.state.patch.sql.patch.Patch;

public class PatchProcessor implements Consumer<Patch> {

    final Database database;

    public PatchProcessor(Database database) {
        this.database = database;
    }

    @Override
    public void accept(Patch patch) {
        for (Operation operation : patch.operations) {
            if (operation instanceof CreateTable) {
                createTable((CreateTable) operation);
            } else if (operation instanceof DeleteTable) {
                deleteTable((DeleteTable) operation);
            }
        }
    }

    private void createTable(CreateTable operation) {
        try (Connection connection = database.datasource.getConnection()) {
            String sql = database.sqlCreateTable(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void deleteTable(DeleteTable operation) {
        try (Connection connection = database.datasource.getConnection()) {
            String sql = database.sqlDeleteTable(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
