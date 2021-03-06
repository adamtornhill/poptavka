package com.eprovement.poptavka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validity of provided activation link expired."
        + " Send a request for generation new activation link.")
public class ExpiredActivationCodeException extends RuntimeException {
    public ExpiredActivationCodeException(String message) {
        super(message);
    }

    public ExpiredActivationCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
