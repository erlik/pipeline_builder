package org.erlik.pipeline_builder.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PipelineValidatorUtilTest {

    private final List<Object> emptyList = List.of();
    private final List<Object> singleElementList = List.of("1");
    private final List<Object> multiElementList = List.of("1", "2", "3");

    @Test
    void notEmptyReturnTrueWithANotEmptyList() {
        var test = PipelineValidatorUtil.notEmpty().test(singleElementList);
        assertThat(test).isTrue();
    }

    @Test
    void notEmptyReturnFalseWithAnEmptyList() {
        var test = PipelineValidatorUtil.notEmpty().test(emptyList);
        assertThat(test).isFalse();
    }

    @Test
    void onlyOneReturnTrueWithASingleElementList() {
        var test = PipelineValidatorUtil.onlyOne().test(singleElementList);
        assertThat(test).isTrue();
    }

    @Test
    void onlyOneReturnFalseWithAMultiElementsList() {
        var test = PipelineValidatorUtil.onlyOne().test(multiElementList);
        assertThat(test).isFalse();
    }

    @Test
    void onlyOneReturnFalseWithAnEmptyList() {
        var test = PipelineValidatorUtil.onlyOne().test(emptyList);
        assertThat(test).isFalse();
    }

    @Test
    void greaterThanOneReturnTrueWithAMultiElementsList() {
        var test = PipelineValidatorUtil.greaterThanOne().test(multiElementList);
        assertThat(test).isTrue();
    }

    @Test
    void greaterThanOneReturnFalseWithASingleElementList() {
        var test = PipelineValidatorUtil.greaterThanOne().test(singleElementList);
        assertThat(test).isFalse();
    }

    @Test
    void greaterThanOneFalseWithAnEmptyList() {
        var test = PipelineValidatorUtil.greaterThanOne().test(emptyList);
        assertThat(test).isFalse();
    }
}
