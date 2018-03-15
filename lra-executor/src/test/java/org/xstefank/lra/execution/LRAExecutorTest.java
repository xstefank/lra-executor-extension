package org.xstefank.lra.execution;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xstefank.lra.definition.LRABuilder;
import org.xstefank.lra.definition.LRADefinition;
import org.xstefank.lra.model.ActionResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class LRAExecutorTest {

    private LRADefinition definition;
    private AtomicInteger counter;

    @Before
    public void before() {
        counter = new AtomicInteger(0);
        definition = null;
    }

    @After
    public void after() {
        counter = null;
    }

    @Test
    public void testSimpleExecution() {
        definition = LRABuilder.lra()
                .name("test LRA")
                .withAction(d -> {
                    counter.getAndIncrement();
                    return ActionResult.success();
                })
                .data(new StringBuilder("mutable"))
                .build();

        LRAExecutorStub lraExecutor = new LRAExecutorStub(definition);
        lraExecutor.executeLRA();

        Assert.assertEquals(1, counter.get());
        Assert.assertEquals("mutable success", ((StringBuilder) definition.getData()).toString());

    }

    @Test
    public void testExecutionFailure() {
        definition = LRABuilder.lra()
                .name("test LRA")
                .withAction(d -> ActionResult.failure())
                .data(new StringBuilder("mutable"))
                .build();

        LRAExecutorStub lraExecutor = new LRAExecutorStub(definition);
        lraExecutor.executeLRA();

        Assert.assertEquals(0, counter.get());
        Assert.assertEquals("mutable failure", ((StringBuilder) definition.getData()).toString());
    }


    private static final class LRAExecutorStub extends AbstractLRAExecutor {

        private LRADefinition definition;

        public LRAExecutorStub(LRADefinition definition) {
            this.definition = definition;
        }

        @SuppressWarnings(value = "unchecked")
        public void executeLRA() {
            super.executeLRA(definition);
        }

        @Override
        protected URL startLRA(LRADefinition lraDefinition) {
            try {
                return new URL("http://stub.lra");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void compensateLRA(URL lraId) {
            ((StringBuilder) definition.getData()).append(" failure");
        }

        @Override
        protected void completeLRA(URL lraId) {
            ((StringBuilder) definition.getData()).append(" success");
        }
    }
}
