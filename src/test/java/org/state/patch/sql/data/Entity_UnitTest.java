package org.state.patch.sql.data;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Entity_UnitTest {

    @Test
    public void constructor() {
        // Setup,
        ReferenceInternal id = new ReferenceInteger("entity", 123L);
        Map<String, Object> attrs = new HashMap<>();

        // Execute
        Entity entity = new Entity(id, attrs);

        //Verify
        assertSame(id, entity.id);
        assertSame(attrs, entity.attrs);
    }

}
