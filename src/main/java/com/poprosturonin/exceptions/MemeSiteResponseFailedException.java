package com.poprosturonin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when there is a problem with connection to the meme site
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MemeSiteResponseFailedException extends RuntimeException {
}
