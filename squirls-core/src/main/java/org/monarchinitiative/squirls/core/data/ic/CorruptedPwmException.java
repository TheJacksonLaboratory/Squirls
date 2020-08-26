package org.monarchinitiative.squirls.core.data.ic;


import org.monarchinitiative.squirls.core.SquirlsException;

/**
 *
 */
public class CorruptedPwmException extends SquirlsException {

    public CorruptedPwmException() {
        super();
    }

    public CorruptedPwmException(String message) {
        super(message);
    }

    public CorruptedPwmException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptedPwmException(Throwable cause) {
        super(cause);
    }

    protected CorruptedPwmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}