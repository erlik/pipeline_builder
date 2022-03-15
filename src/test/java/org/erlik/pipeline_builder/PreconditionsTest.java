package org.erlik.pipeline_builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PreconditionsTest {

    @Test
    public void throwsIfArgumentIsNull() {
        var msg = "Parameter must not be null";
        Throwable e =
            assertThrows(
                IllegalArgumentException.class,
                () -> Preconditions.isNotNull(null));

        assertThat(e).hasMessage(msg);
    }

    @Test
    public void throwsReturnMessageIfArgumentIsNull() {
        var msg = "Shit has happened";
        Throwable e =
            assertThrows(
                IllegalArgumentException.class,
                () -> Preconditions.isNotNull(null, msg));

        assertThat(e).hasMessage(msg);
    }

    @Test
    public void returnsAnArgumentWithoutMsgIfTheArgumentIsAValidObject() {
        // given
        Object checkedObject = new Object();

        // when
        Object returnedObject = Preconditions.isNotNull(checkedObject, "OK");

        // then
        assertThat(returnedObject).isEqualTo(checkedObject);
    }

    @Test
    public void returnsAnArgumentIfTheArgumentIsAValidObject() {
        // given
        Object checkedObject = new Object();

        // when
        Object returnedObject = Preconditions.isNotNull(checkedObject);

        // then
        assertThat(returnedObject).isEqualTo(checkedObject);
    }
}
