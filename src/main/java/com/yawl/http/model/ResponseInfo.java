package com.yawl.http.model;

/**
 * Holds response metadata for a route handler, including content type and HTTP status.
 *
 * @param contentType the content type of the response
 * @param status      the HTTP status code
 */
public record ResponseInfo(ContentType contentType, HttpStatus status) {
}
