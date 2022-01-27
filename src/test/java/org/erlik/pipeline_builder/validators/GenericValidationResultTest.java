package org.erlik.pipeline_builder.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GenericValidationResultTest {

    @Test
    void throwsValidateWhenInitWithOk() {
        var result = GenericValidationResult.ok();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void throwsValidateWhenInitWithFail() {
        var result = GenericValidationResult.fail();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void shouldThrowAnExceptionExceptionWhenValueIsFalse() {
        var e =
            assertThrows(
                Exception.class,
                () -> GenericValidationResult.fail()
                    .orThrow(() -> new Exception("Throw Exception")));

        assertThat(e)
            .hasMessage(
                "Throw Exception");
    }

    @Test
    void notShouldThrowAnExceptionWhenValueIsTrue() throws Exception {
        var e = GenericValidationResult.ok().orThrow(() -> new Exception("Throw Exception"));
        assertThat(e).isTrue();

    }
}
