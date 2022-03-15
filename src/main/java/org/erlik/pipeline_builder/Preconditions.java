package org.erlik.pipeline_builder;

import java.util.Objects;

public class Preconditions {

    private Preconditions() {
    }

    public static <TObject> TObject isNotNull(TObject nonNullable, String errorMessage) {
        if (Objects.isNull(nonNullable)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return nonNullable;
    }

    public static <TObject> TObject isNotNull(TObject nonNullable) {
        return isNotNull(nonNullable, "Parameter must not be null");
    }
}
