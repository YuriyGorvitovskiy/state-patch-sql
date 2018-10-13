package org.state.patch.sql.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class ResourceString {

    final Class<?>   resourceClass;
    final String     resourceName;
    transient String resourceString;

    public ResourceString(Class<?> resourceClass, String resourceName) {
        this.resourceClass = resourceClass;
        this.resourceName = resourceName;
        this.resourceString = null;
    }

    @Override
    public String toString() {
        if (null != resourceString) {
            return resourceString;
        }
        try (InputStream input = resourceClass.getResourceAsStream(resourceName)) {
            resourceString = IOUtils.toString(input, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            throw new RuntimeException("Can't load " + describe(), ex);
        }
        return resourceString;
    }

    private String describe() {
        return "resource '" + resourceName + "' related to " + resourceClass.getName() + " class";
    }

}
