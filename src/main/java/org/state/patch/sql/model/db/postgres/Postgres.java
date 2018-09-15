package org.state.patch.sql.model.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.ValueType;
import org.state.patch.sql.model.db.Database;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

public class Postgres implements Database {

    public static final String ENGINE = "POSTGRES";

    final Model           model;
    final DatabaseConfig  config;
    final BasicDataSource datasource;

    public Postgres(Model model, DatabaseConfig config) {
        this.model = model;
        this.config = config;
        this.datasource = new BasicDataSource();
        this.datasource.setDriverClassName(config.driver);
        this.datasource.setUrl(config.url);
        this.datasource.setUsername(config.username);
        this.datasource.setPassword(config.password);
    }

    @Override
    public void createType(ModelOpCreateType op) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.type);
        sql.append(" (\n    ");

        sql.append(op.identity.name);
        sql.append("  ");
        sql.append(toSQLType(op.identity.type));
        for (ModelOp.Attribute attr : op.attrs) {
            sql.append(",\n    ");
            sql.append(attr.name);
            sql.append("  ");
            sql.append(toSQLType(attr.type));
        }
        sql.append(",\n    ");
        sql.append("PRIMARY KEY (");
        sql.append(op.identity.name);
        sql.append(")\n");
        sql.append(");");

        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.execute();
        }

        executeSql(sql.toString());
    }

    @Override
    public void deleteType(ModelOpDeleteType op) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.type);
        sql.append(" CASCADE;");

        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.execute();
        }

        executeSql(sql.toString());
    }

    @Override
    public void appendAttribute(ModelOpAppendAttribute op) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.type);
        sql.append("\n");
        sql.append("    ADD COLUMN ");
        sql.append(op.attr.name);
        sql.append("  ");
        sql.append(toSQLType(op.attr.type));
        sql.append(";");

        executeSql(sql.toString());
    }

    @Override
    public void deleteAttribute(ModelOpDeleteAttribute op) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.type);
        sql.append("\n");
        sql.append("    DROP COLUMN ");
        sql.append(op.attribName);
        sql.append(";");

        executeSql(sql.toString());
    }

    @Override
    public void insert(DataOpInsert op) throws Exception {
        StringBuilder sql = new StringBuilder();
        List<Pair<ValueType, Object>> params = new ArrayList<>(1 + op.attrs.size());
        sql.append("INSERT INTO ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.id.type);
        sql.append(" (");

        EntityType entityType = model.getEntityType(op.id.type);
        sql.append(entityType.identity.name);
        params.add(new ImmutablePair<>(entityType.identity.type, op.id));

        for (Map.Entry<String, Object> entry : op.attrs.entrySet()) {
            Attribute attr = entityType.attrs.get(entry.getKey());
            if (null == attr) {
                continue;
            }
            sql.append(", ");
            sql.append(attr.name);
            params.add(new ImmutablePair<>(attr.type, entry.getValue()));
        }

        sql.append(")\n");
        sql.append("    VALUES (");
        sql.append(StringUtils.repeat("?", ", ", params.size()));
        sql.append(")\n");
        sql.append(";");

        executeSql(sql.toString(), params);
    }

    @Override
    public void update(DataOpUpdate op) throws Exception {
        StringBuilder sql = new StringBuilder();
        List<Pair<ValueType, Object>> params = new ArrayList<>(1 + op.attrs.size());

        sql.append("UPDATE ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.id.type);
        sql.append(" SET ");

        EntityType entityType = model.getEntityType(op.id.type);

        String separator = "";
        for (Map.Entry<String, Object> entry : op.attrs.entrySet()) {
            Attribute attr = entityType.attrs.get(entry.getKey());
            if (null == attr) {
                continue;
            }
            sql.append(separator);
            sql.append(attr.name);
            sql.append(" = ?");
            params.add(new ImmutablePair<>(attr.type, entry.getValue()));
            separator = ", ";
        }
        sql.append(")\n");
        sql.append("    WHERE (");
        sql.append(entityType.identity.name);
        sql.append(" = ?)\n");
        params.add(new ImmutablePair<>(entityType.identity.type, op.id));
        sql.append(";");

        executeSql(sql.toString(), params);
    }

    @Override
    public void delete(DataOpDelete op) throws Exception {
        StringBuilder sql = new StringBuilder();
        List<Pair<ValueType, Object>> params = new ArrayList<>(1);

        sql.append("DELETE FROM ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(op.id.type);
        sql.append("\n");

        EntityType entityType = model.getEntityType(op.id.type);

        sql.append("    WHERE (");
        sql.append(entityType.identity.name);
        sql.append(" = ?)\n");
        params.add(new ImmutablePair<>(entityType.identity.type, op.id));
        sql.append(";");

        executeSql(sql.toString(), params);
    }

    @Override
    public List<Entity> select(Collection<ReferenceInternal> ids,
                               Collection<Attribute> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Entity> select(String entityType,
                               Collection<Attribute> attributes,
                               Map<String, Object> conditions,
                               Map<String, Boolean> sorting) {
        // TODO Auto-generated method stub
        return null;
    }

    private void executeSql(String sql) throws Exception {
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.execute();
        }
    }

    private void executeSql(String sql, List<Pair<ValueType, Object>> params) throws Exception {
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            for (Pair<ValueType, Object> param : params) {
                setValue(ps, index++, param.getLeft(), param.getRight());
            }
            ps.execute();
        }
    }

    private void setValue(PreparedStatement ps, int pos, ValueType type, Object value) throws Exception {
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).storageType;
            if (PrimitiveType.INTEGER == type) {
                value = ((ReferenceInteger) value).id;
            } else if (PrimitiveType.STRING == type) {
                value = ((ReferenceString) value).id;
            } else {
                throw new RuntimeException("Unsupported reference type: " + type);
            }
        }
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    if (null != value) {
                        ps.setBoolean(pos, (Boolean) value);
                    } else {
                        ps.setNull(pos, Types.BOOLEAN);
                    }
                    return;
                case DOUBLE:
                    if (null != value) {
                        ps.setDouble(pos, ((Number) value).doubleValue());
                    } else {
                        ps.setNull(pos, Types.DOUBLE);
                    }
                    return;
                case INTEGER:
                    if (null != value) {
                        ps.setLong(pos, ((Number) value).longValue());
                    } else {
                        ps.setNull(pos, Types.BIGINT);
                    }
                    return;
                case STRING:
                case TEXT:
                case REFERENCE_EXTERNAL:
                    if (null != value) {
                        ps.setString(pos, Objects.toString(value));
                    } else {
                        ps.setNull(pos, Types.VARCHAR);
                    }
                    return;
                case TIMESTAMP:
                    if (null != value) {
                        ps.setTimestamp(pos, new Timestamp(((Date) value).getTime()));
                    } else {
                        ps.setNull(pos, Types.VARCHAR);
                    }
                    return;
            }
        }
        throw new RuntimeException("Unsupported value type: " + type);
    }

    private String toSQLType(ValueType type) {
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).storageType;
        }
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BOOLEAN:
                    return "boolean";
                case DOUBLE:
                    return "double precision";
                case INTEGER:
                    return "bigint";
                case REFERENCE_EXTERNAL:
                    return "character varying (256)";
                case STRING:
                    return "character varying (256)";
                case TEXT:
                    return "text";
                case TIMESTAMP:
                    return "timestamp with time zone";
            }
        }
        throw new RuntimeException("Unsupported value type: " + type);
    }
}
