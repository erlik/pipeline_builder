package org.erlik.pipeline_builder.validators;

import java.util.function.Supplier;

public record GenericValidationResult(boolean isValid) {

    public static GenericValidationResult ok() {
        return new GenericValidationResult(true);
    }

    public static GenericValidationResult fail() {
        return new GenericValidationResult(false);
    }

    public <TThrowable extends Throwable> boolean orThrow(Supplier<? extends TThrowable> exceptionSupplier)
        throws TThrowable {
        if (!isValid) {
            throw exceptionSupplier.get();
        }
        return true;
    }
}
