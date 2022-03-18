package org.erlik.pipeline_builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class PipelineHandlerTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void resolvesHandlersWithAGenericRequestType() {
        Pipeline requestBus = new PipelineBuilder()
            .handlers(() -> Stream.of(new GenericPipelineTypeHandler()));
        var request = new FooRequest<>(new BarRequest());

        String result = requestBus.submit(request)
            .first();
        assertThat(result).isEqualTo("BarRequest");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getHandlerListfromDispatcher() {
        var expectedHeadler = new GenericPipelineTypeHandler();

        Pipeline requestBus = new PipelineBuilder()
            .handlers(() -> Stream.of(new GenericPipelineTypeHandler()));
        var request = new FooRequest<>(new BarRequest());


        List<PipelineHandler> result = requestBus.submit(request)
                .handlers();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getClass()).isEqualTo(expectedHeadler.getClass());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getHandlerfromDispatcher() {
        var expectedHeadler = new GenericPipelineTypeHandler();

        Pipeline requestBus = new PipelineBuilder()
            .handlers(() -> Stream.of(new GenericPipelineTypeHandler()));
        var request = new FooRequest<>(new BarRequest());


       PipelineHandler result = requestBus.submit(request)
                .handler();
        assertThat(result.getClass()).isEqualTo(expectedHeadler.getClass());
    }

    @Test
    public void handlesQueriesThatAreSubtypesOfAGenericArgument() {
        // given
        var pingHandler = new PingHandler();
        var notAPingHandler = new NotAPingHandler();
        Pipeline requestBus = new PipelineBuilder().handlers(() -> Stream.of(
            pingHandler,
            notAPingHandler));

        // and
        var ping = new PingRequest();
        var smartPing = new SmartPingRequest();
        var notAPing = new NotAPing();

        // when
        requestBus.submit(ping).dispatch();
        requestBus.submit(smartPing).dispatch();
        requestBus.submit(notAPing).dispatch();

        // then
        assertThat(pingHandler.handled).containsOnly(
            ping.getClass().getSimpleName(),
            smartPing.getClass().getSimpleName());
        assertThat(notAPingHandler.handled).containsOnly(notAPing.getClass().getSimpleName());
    }

    private record BarRequest()
        implements Serializable {

    }

    private record FooRequest<C>(C request)
        implements Serializable {

    }

    private static class GenericPipelineTypeHandler<C, R>
        implements PipelineHandler<FooRequest<C>, R> {

        @Override
        public R handleRequest(FooRequest<C> request) {
            return (R) request.request().getClass().getSimpleName();
        }
    }

    private static class PingRequest
        implements Serializable {

    }

    private static class PingHandler
        implements PipelineHandler<PingRequest, List<String>> {

        private final List<String> handled = new ArrayList<>();

        @Override
        public List<String> handleRequest(PingRequest request) {
            handled.add(request.getClass().getSimpleName());
            return handled;
        }
    }

    private static class SmartPingRequest
        extends PingRequest {

    }

    private static class NotAPing
        implements Serializable {

    }

    private static class NotAPingHandler
        implements PipelineHandler<NotAPing, List<String>> {

        private final List<String> handled = new ArrayList<>();

        @Override
        public List<String> handleRequest(NotAPing request) {
            handled.add(request.getClass().getSimpleName());
            return handled;
        }
    }
}
