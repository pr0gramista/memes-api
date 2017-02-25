package com.poprosturonin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is raised when parsing fails
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ParseErrorException extends RuntimeException {
}