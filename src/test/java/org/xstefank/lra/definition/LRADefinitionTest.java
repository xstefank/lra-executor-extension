package org.xstefank.lra.definition;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LRADefinitionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testDefinitionSimple() {
        LRADefinition definition = new LRABuilder()
                .name("test LRA")
                .withAction(new DummyAction())
                .data("test data")
                .build();

        Assert.assertEquals("test LRA", definition.getName());
        Assert.assertEquals(1, definition.getActions().size());
        Assert.assertEquals(String.class, definition.getData().getClass());
        Assert.assertEquals("test data", definition.getData());
    }

    @Test
    public void testEmptyActions() {
        expectedException.expect(IllegalArgumentException.class);

        LRADefinition definition = new LRABuilder()
                .name("test LRA")
                .data("test data")
                .build();
    }

    @Test
    public void testMultipleActionsNoData() {
        LRADefinition definition = new LRABuilder()
                .name("test LRA")
                .withAction(new DummyAction())
                .withAction(new DummyAction())
                .build();

        Assert.assertEquals("test LRA", definition.getName());
        Assert.assertEquals(2, definition.getActions().size());
        Assert.assertNull(definition.getData());
    }

    private static class DummyAction implements Action {

        public String getName() {
            return "Dummy action";
        }

        @Override
        public void invoke(Object data) {
            System.out.println(getName() + "invocation");
        }
    }

}
