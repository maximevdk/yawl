package com.yawl.http.model;

/**
 * Represents an HTTP content type (MIME type).
 *
 * @param value the MIME type string
 */
public record ContentType(String value) {
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final ContentType APPLICATION_JSON = new ContentType(APPLICATION_JSON_VALUE);
    public static final ContentType TEXT_PLAIN = new ContentType(TEXT_PLAIN_VALUE);

    /**
     * Creates a {@code ContentType} from the given MIME type string.
     *
     * @param value the MIME type string
     * @return a new content type instance
     */
    public static ContentType of(String value) {
        return new ContentType(value);
    }
}
