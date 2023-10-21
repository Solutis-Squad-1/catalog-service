package br.com.solutis.squad1.catalogservice.exception;

import lombok.Getter;

@Getter
public class ImageException extends RuntimeException {
    public ImageException(String message) {
        super(message);
    }
}
