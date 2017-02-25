package com.poprosturonin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is raised when login for scrapping some sites
 * fails fe. 9gag NSFW section
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class LoginFailedException extends RuntimeException {
}