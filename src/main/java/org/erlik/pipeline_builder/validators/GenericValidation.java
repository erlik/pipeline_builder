package org.erlik.pipeline_builder.validators;

import java.util.function.Predicate;

public record GenericValidation<TParam>(TParam param) {

    public static <TParam> GenericValidation<TParam> from(TParam param) {
        return new GenericValidation<>(param);
    }

    public GenericValidationResult expected(Predicate<TParam> predicate) {
        return predicate.test(param)
               ? GenericValidationResult.ok()
               : GenericValidationResult.fail();
    }

}
