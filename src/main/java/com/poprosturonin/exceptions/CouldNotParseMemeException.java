package com.poprosturonin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Parsing meme failed
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CouldNotParseMemeException extends RuntimeException {
}
