package org.erlik.pipeline_builder.validators;

import java.util.List;
import java.util.function.Predicate;

public class PipelineValidatorUtil {

    private PipelineValidatorUtil() {
    }

    public static <TObject> Predicate<List<TObject>> notEmpty() {
        return s -> !s.isEmpty();
    }

    public static <TObject> Predicate<List<TObject>> onlyOne() {
        return s -> s.size() == 1;
    }

    public static <TObject> Predicate<List<TObject>> greaterThanOne() {
        return s -> s.size() > 1;
    }

}
