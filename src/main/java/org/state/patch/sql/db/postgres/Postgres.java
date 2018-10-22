package org.state.patch.sql.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.data.Entity;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceInternal;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.db.Database;
import org.state.patch.sql.model.Attribute;
import org.state.patch.sql.model.EntityType;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.ValueType;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class Postgres implements Database {

    @FunctionalInterface
    public static interface ResultRowConsumer {
        public void accept(ResultSet rs) throws Exception;
    }

    public static final String ENGINE = "POSTGRES";

    public static final String        DATE_TIMEZONE = "UTC";
    public static final String        DATE_PATTERN  = "yyyy-MM-dd HH:mm:ss.SSS+0";
    public static final StdDateFormat DATE_PARSE    = new StdDateFormat();

    @SuppressWarnings("serial")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN) {
        {
            this.setTimeZone(TimeZone.getTimeZone(DATE_TIMEZONE));
        }
    };

    public final Model           model;
    public final DatabaseConfig  config;
    public final BasicDataSource datasource;

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
    public Model getModel() {
        return model;
    }

    @Override
    public boolean isTypeExists(String type) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT EXISTS (\n");
        sql.append("   SELECT 1\n");
        sql.append("       FROM  pg_tables\n");
        sql.append("       WHERE   schemaname = ?\n");
        sql.append("           AND tablename = ?\n");
        sql.append(");");

        List<Pair<ValueType, Object>> params = new ArrayList<>(2);
        params.add(new ImmutablePair<>(PrimitiveType.STRING, config.schema));
        params.add(new ImmutablePair<>(PrimitiveType.STRING, type));

        MutableBoolean isExists = new MutableBoolean(false);
        executeSqlQuery(sql.toString(), params, (rs) -> {
            if (rs.next()) {
                isExists.setValue(rs.getBoolean(1));
            }
        });

        return isExists.booleanValue();
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
            if (null != attr.initial) {
                sql.append(" DEFAULT ");
                sql.append(toSQLLiteral(attr.type, attr.initial));
            }
        }
        sql.append(",\n    ");
        sql.append("PRIMARY KEY (");
        sql.append(op.identity.name);
        sql.append(")\n");
        sql.append(");");

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
        if (null != op.attr.initial) {

        }
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
    public List<Entity> select(Collection<Attribute> attributes,
                               EntityType entityType,
                               Collection<Pair<Attribute, Collection<?>>> conditions,
                               Collection<Pair<Attribute, Boolean>> sortings) throws Exception {

        StringBuilder sql = new StringBuilder();
        List<Pair<ValueType, Object>> params = new ArrayList<>(1);

        sql.append("SELECT ");
        sql.append(entityType.identity.name);
        for (Attribute attribute : attributes) {
            sql.append(", ");
            sql.append(attribute.name);
        }
        sql.append("\n    FROM ");
        sql.append(config.schema);
        sql.append(".");
        sql.append(entityType.name);
        if (null != conditions && !conditions.isEmpty()) {
            String separator = "\n    WHERE ";
            for (Pair<Attribute, Collection<?>> condition : conditions) {
                Attribute attribute = condition.getLeft();
                Collection<?> values = condition.getRight();

                sql.append(separator);
                sql.append(attribute.name);
                if (1 == values.size()) {
                    sql.append(" = ?");
                } else {
                    sql.append(" IN (");
                    StringUtils.repeat("?", ", ", values.size());
                    sql.append(")");
                }
                for (Object value : values) {
                    params.add(new ImmutablePair<>(attribute.type, value));
                }
                separator = "\n     AND ";
            }
        }
        if (null != sortings && !sortings.isEmpty()) {
            String separator = "\n    ORDER BY ";
            for (Pair<Attribute, Boolean> sorting : sortings) {
                sql.append(separator);
                sql.append(sorting.getLeft().name);
                sql.append(sorting.getRight() ? " ASC" : " DESC");
                separator = "\n            ,";
            }
        }
        List<Entity> entities = new ArrayList<>();
        executeSqlQuery(sql.toString(),
                        params,
                        (rs) -> {
                            while (rs.next()) {
                                int pos = 1;
                                ReferenceInternal id = (ReferenceInternal) getValue(rs, pos++, entityType.identity.type);
                                Map<String, Object> attrs = new HashMap<>();
                                for (Attribute attribute : attributes) {
                                    // keep nulls in attribute table.
                                    attrs.put(attribute.name, getValue(rs, pos++, attribute.type));
                                }
                                Entity entity = new Entity(id, Collections.unmodifiableMap(attrs));
                                entities.add(entity);
                            }
                        });
        return entities;
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

    private void executeSqlQuery(String sql,
                                 List<Pair<ValueType, Object>> params,
                                 ResultRowConsumer rowConsumer) throws Exception {
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            for (Pair<ValueType, Object> param : params) {
                setValue(ps, index++, param.getLeft(), param.getRight());
            }

            try (ResultSet rs = ps.executeQuery()) {
                rowConsumer.accept(rs);
            }
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

    private Object getValue(ResultSet rs, int pos, ValueType type) throws Exception {
        if (type instanceof PrimitiveType) {
            return getValue(rs, pos, (PrimitiveType) type);
        } else if (type instanceof ReferenceType) {
            ReferenceType referenceType = (ReferenceType) type;
            Object value = getValue(rs, pos, referenceType.storageType);
            if (null == value) {
                return null;
            } else if (PrimitiveType.INTEGER == referenceType.storageType) {
                return new ReferenceInteger(referenceType.entityType, (Long) value);
            } else if (PrimitiveType.STRING == referenceType.storageType) {
                return new ReferenceString(referenceType.entityType, (String) value);
            }
        }
        throw new RuntimeException("Unsupported value type: " + type);
    }

    private Object getValue(ResultSet rs, int pos, PrimitiveType type) throws Exception {
        switch (type) {
            case BOOLEAN: {
                boolean value = rs.getBoolean(pos);
                return rs.wasNull() ? null : value;
            }
            case DOUBLE: {
                double value = rs.getDouble(pos);
                return rs.wasNull() ? null : value;
            }
            case INTEGER: {
                long value = rs.getLong(pos);
                return rs.wasNull() ? null : value;
            }
            case REFERENCE_EXTERNAL: {
                String value = rs.getString(pos);
                return rs.wasNull() ? null : new ReferenceExternal(value);
            }
            case STRING:
            case TEXT: {
                return rs.getString(pos);
            }
            case TIMESTAMP: {
                Timestamp value = rs.getTimestamp(pos);
                return rs.wasNull() ? null : new Date(value.getTime());
            }
        }
        throw new RuntimeException("Unsupported primitive type: " + type);
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

    private String toSQLLiteral(ValueType type, Object value) {
        if (null == value) {
            return "NULL";
        }
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
                    return ((Boolean) value) ? "TRUE" : "FALSE";
                case DOUBLE:
                    return Double.toString(((Number) value).doubleValue());
                case INTEGER:
                    return Long.toString(((Number) value).longValue());
                case REFERENCE_EXTERNAL:
                case STRING:
                case TEXT:
                    return "'" + value.toString().replaceAll("'", "''") + "'";
                case TIMESTAMP:
                    return "timestamptz '" + DATE_FORMAT.format((Date) value) + "'";
            }
        }
        throw new RuntimeException("Unsupported value type: " + type);
    }

}
