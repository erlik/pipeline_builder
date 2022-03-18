package org.erlik.pipeline_builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.erlik.pipeline_builder.exceptions.ValidatorException;
import org.erlik.pipeline_builder.validators.GenericValidation;
import org.erlik.pipeline_builder.validators.PipelineValidatorUtil;
import org.erlik.pipeline_builder.validators.ValidationFailedException;
import org.junit.jupiter.api.Test;

public class PipelineValidatorsTest {

    @Test
    public void supportsAbstractRequestHandlers() {
        // given
        HandlerThatExtendsAbstractClass handlerThatExtendsAbstractClass =
            new HandlerThatExtendsAbstractClass();

        // and
        Pipeline pipeline = new PipelineBuilder()
            .handlers(() -> Stream.of(handlerThatExtendsAbstractClass));

        // when
        pipeline.submit(new PingRequest("hi"))
            .validate(requestHandlers -> GenericValidation.from(requestHandlers)
                .expected(PipelineValidatorUtil.onlyOne())
                .isValid()).dispatch();

        // then:
        assertThat(handlerThatExtendsAbstractClass.receivedPingRequests)
            .containsOnly(new PingRequest("hi"));
    }

    @Test
    public void supportsCustomHandlerMatching() {
        // given
        HiHandler hiHandler = new HiHandler();
        PingSaverHandler pingSaverHandler = new PingSaverHandler();

        // and
        Pipeline pipeline = new PipelineBuilder()
            .handlers(() -> Stream.of(hiHandler,
                pingSaverHandler));

        // when
        pipeline.submit(new PingRequest("hi")).dispatch();

        // and
        pipeline.submit(new PingRequest("bye")).dispatch();

        // then
        assertThat(hiHandler.receivedPingRequests)
            .containsOnly(new PingRequest("hi"));

        // and
        assertThat(pingSaverHandler.receivedPingRequests)
            .containsOnly(new PingRequest("bye"));
    }

    @Test
    public void throwsIfSentRequestHasNoMatchingHandler() {
        // given
        PipelineBuilder requestPipeline = new PipelineBuilder();
        PingRequest pingRequest = new PingRequest();
        // when
        Throwable e =
            assertThrows(
                ValidatorException.class,
                () -> requestPipeline
                    .submit(pingRequest)
                    .validate(h -> GenericValidation.from(h)
                        .expected(PipelineValidatorUtil.notEmpty())
                        .orThrow(ValidatorException::new))
                    .dispatch());

        // then
        assertThat(e)
            .hasMessage("Validator exception");
    }

    @Test
    public void throwsIfSentRequestHasMultipleHandlers() {
        // given
        PingRequest pingRequest = new PingRequest();
        Pipeline pipeline = new PipelineBuilder()
            .handlers(() -> Stream.of(new Pong1Handler(),
                new Pong2Handler()));

        // when
        Throwable e =
            assertThrows(
                ValidatorException.class,
                () -> pipeline
                    .submit(pingRequest)
                    .validate(requestHandlers -> GenericValidation.from(requestHandlers)
                        .expected(PipelineValidatorUtil.onlyOne())
                        .orThrow(ValidatorException::new))
                    .dispatch());

        // then
        assertThat(e)
            .hasMessage("Validator exception");
    }

    @Test
    public void throwsDefaultException() {
        // given
        PingRequest pingRequest = new PingRequest();
        Pipeline pipeline = new PipelineBuilder()
            .handlers(() -> Stream.of(new Pong1Handler(),
                new Pong2Handler()));

        // when
        Throwable e =
            assertThrows(
                ValidationFailedException.class,
                () -> pipeline
                    .submit(pingRequest)
                    .validate(requestHandlers -> GenericValidation.from(requestHandlers)
                        .expected(PipelineValidatorUtil.onlyOne())
                        .isValid())
                    .dispatch());

        // then
        assertThat(e)
            .hasMessage("The pipeline validator failed");
    }

    private record PingRequest(String message)
        implements Serializable {

        PingRequest() {
            this("Ping");
        }
    }

    private static class PingSaverHandler
        implements PipelineHandler<PingRequest, String> {

        private final Collection<PingRequest> receivedPingRequests = new ArrayList<>();

        @Override
        public String handleRequest(PingRequest request) {
            this.receivedPingRequests.add(request);
            return receivedPingRequests.toString();
        }

        @Override
        public boolean matches(PingRequest request) {
            return request.message.equals("bye");
        }
    }

    private static class HiHandler
        implements PipelineHandler<PingRequest, String> {

        private final Collection<PingRequest> receivedPingRequests = new ArrayList<>();

        @Override
        public String handleRequest(PingRequest request) {
            this.receivedPingRequests.add(request);
            return receivedPingRequests.toString();
        }

        @Override
        public boolean matches(PingRequest request) {
            return request.message.equals("hi");
        }
    }

    private static class Pong1Handler
        implements PipelineHandler<PingRequest, String> {

        @Override
        public String handleRequest(PingRequest request) {
            return "Pong 1";
        }
    }

    private static class Pong2Handler
        implements PipelineHandler<PingRequest, String> {

        @Override
        public String handleRequest(PingRequest request) {
            return "Pong 2";
        }
    }

    private abstract static class AbstractHandler<C, R>
        implements PipelineHandler<C, R> {

    }

    private static class HandlerThatExtendsAbstractClass
        extends AbstractHandler<PingRequest, String> {

        private final Collection<PingRequest> receivedPingRequests = new ArrayList<>();

        @Override
        public String handleRequest(PingRequest request) {
            this.receivedPingRequests.add(request);
            return receivedPingRequests.toString();
        }
    }
}
