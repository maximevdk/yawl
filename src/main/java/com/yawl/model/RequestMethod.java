package com.yawl.model;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

public record RequestMethod(String name, MethodHandle instance, List<RequestParam> parameters, MediaType produces,
                            HttpStatus status) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private MethodHandle instance;
        private List<RequestParam> parameters;
        private MediaType mediaType;
        private HttpStatus status;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder instance(MethodHandle instance) {
            this.instance = instance;
            return this;
        }

        public Builder parameter(RequestParam parameter) {
            if (parameters == null) {
                parameters = new ArrayList<>();
            }

            parameters.add(parameter);
            return this;
        }

        public Builder addParameters(List<? extends RequestParam> requestParameters) {
            if (parameters == null) {
                parameters = new ArrayList<>(requestParameters);
            } else {
                parameters.addAll(requestParameters);
            }

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
            return new RequestMethod(name, instance, parameters, mediaType, status);
        }

    }
}
