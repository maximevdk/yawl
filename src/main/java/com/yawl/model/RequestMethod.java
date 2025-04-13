package com.yawl.model;

import java.util.ArrayList;
import java.util.List;

public record RequestMethod(String name, List<RequestParameter> parameters, MediaType produces, HttpStatus status) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<RequestParameter> parameters;
        private MediaType mediaType;
        private HttpStatus status;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parameter(RequestParameter parameter) {
            if (parameters == null) {
                parameters = new ArrayList<>();
            }

            parameters.add(parameter);
            return this;
        }

        public Builder parameters(List<RequestParameter> parameters) {
            this.parameters = new ArrayList<>(parameters);
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public RequestMethod build() {
            return new RequestMethod(name, parameters, mediaType, status);
        }

    }
}
