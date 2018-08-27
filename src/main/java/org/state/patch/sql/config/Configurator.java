package org.state.patch.sql.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
 * Extract System properties into configuration structure, base on dot notation and prefix.
 */
public class Configurator {

    public static <T> T extract(String prefix, T config) {
        for (Field field : config.getClass().getFields()) {
            int mod = field.getModifiers();

            if (field.isSynthetic()
                    || Modifier.isStatic(mod)
                    || !Modifier.isPublic(mod)
                    || Modifier.isTransient(mod)) {
                continue;
            }

            String key = prefix + "." + field.getName();
            Object value = System.getProperty(key);

            try {
                Class<?> clazz = field.getType();
                if (String.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else {
                        field.set(config, value);
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Boolean) {
                        field.set(config, ((Boolean) value).booleanValue());
                    } else {
                        field.set(config, Boolean.parseBoolean(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).longValue());
                    } else {
                        field.set(config, Long.parseLong(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).intValue());
                    } else {
                        field.set(config, Integer.parseInt(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).shortValue());
                    } else {
                        field.set(config, Short.parseShort(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Byte.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).byteValue());
                    } else {
                        field.set(config, Byte.parseByte(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).doubleValue());
                    } else {
                        field.set(config, Double.parseDouble(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
                    if (null == value) {
                        value = field.get(config);
                    } else if (value instanceof Number) {
                        field.set(config, ((Number) value).floatValue());
                    } else {
                        field.set(config, Float.parseFloat(Objects.toString(value)));
                    }
                    System.out.println("    " + key + "=" + value);
                } else if (Map.class.isAssignableFrom(clazz)) {
                    @SuppressWarnings({ "rawtypes" })
                    Map map = (Map) field.get(config);
                    extractMap(key, map);
                } else {
                    extract(key, field.get(config));
                }
            } catch (Throwable ex) {
                System.err.println("Failed to extract property: '" + key + "' with value: " + value + "'");
                ex.printStackTrace();
            }
        }
        return config;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map extractMap(String prefix, Map config) {
        prefix = prefix + ".";
        for (Map.Entry<?, ?> property : System.getProperties().entrySet()) {
            String propKey = Objects.toString(property.getKey());
            if (propKey.startsWith(prefix)) {
                Object value = property.getValue();
                config.put(propKey.substring(prefix.length()), value);

            }
        }
        for (Map.Entry entry : (Set<Map.Entry>) config.entrySet()) {
            System.out.println("    " + prefix + entry.getKey() + "=" + entry.getValue());
        }
        return config;
    }

}
