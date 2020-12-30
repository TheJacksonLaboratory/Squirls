package org.monarchinitiative.squirls.io;

import java.io.IOException;

/**
 * Checked exception which is thrown when there are issues with constructing a Squirls domain object from provided
 * input.
 */
public class SquirlsSerializationException extends IOException {

    public SquirlsSerializationException() {
        super();
    }

    public SquirlsSerializationException(String message) {
        super(message);
    }

    public SquirlsSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SquirlsSerializationException(Throwable cause) {
        super(cause);
    }
}
