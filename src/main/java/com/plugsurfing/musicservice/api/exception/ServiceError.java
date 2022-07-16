package com.plugsurfing.musicservice.api.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ServiceError(HttpStatus status,
                           String errorMessage,
                           LocalDateTime timestamp) {
  public ServiceError(HttpStatus status, String message) {
    this(status, message, LocalDateTime.now());
  }
}
