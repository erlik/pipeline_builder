package org.erlik.pipeline_builder.validators;

public class ValidationFailedException
    extends RuntimeException {

    public ValidationFailedException() {
        super("The pipeline validator failed");
    }
}
