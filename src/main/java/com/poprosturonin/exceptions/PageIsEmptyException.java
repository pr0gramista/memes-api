package com.poprosturonin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is raised when requested site doesn't contain
 * valuable data.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PageIsEmptyException extends RuntimeException {
}
