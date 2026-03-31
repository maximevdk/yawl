package com.yawl.model;

import com.yawl.http.model.HttpStatus;

/**
 * Snapshot of the application's runtime health metrics.
 *
 * @param availableMemory the available memory in bytes
 * @param totalMemory     the total memory in bytes
 * @param usedMemory      the used memory in bytes
 * @param cpuUsage        the CPU usage as a fraction between 0 and 1
 */
public record Health(Long availableMemory, Long totalMemory, Long usedMemory, double cpuUsage) {

    private static Health newHealth(Builder builder) {
        return new Health(builder.availableMemory, builder.totalMemory, builder.totalMemory - builder.availableMemory, builder.cpuUsage);
    }

    /**
     * Creates a new health builder.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents the overall health status of the application.
     */
    public enum Status {
        /** The application is running normally. */
        UP(HttpStatus.OK),
        /** The application is down or unhealthy. */
        DOWN(HttpStatus.ERROR),
        /** The application is starting up. */
        STARTING(HttpStatus.ERROR);

        private final HttpStatus status;

        Status(HttpStatus status) {
            this.status = status;
        }

        /**
         * Returns the HTTP status associated with this health status.
         *
         * @return the corresponding HTTP status
         */
        public HttpStatus getStatus() {
            return status;
        }
    }

    /**
     * Builder for constructing {@link Health} instances.
     */
    public static class Builder {
        /** Creates a new builder. */
        public Builder() {}

        private Long availableMemory;
        private Long totalMemory;
        private double cpuUsage;

        /**
         * Sets the available memory.
         *
         * @param availableMemory the available memory in bytes
         * @return this builder
         */
        public Builder availableMemory(Long availableMemory) {
            this.availableMemory = availableMemory;
            return this;
        }

        /**
         * Sets the total memory.
         *
         * @param totalMemory the total memory in bytes
         * @return this builder
         */
        public Builder totalMemory(Long totalMemory) {
            this.totalMemory = totalMemory;
            return this;
        }

        /**
         * Sets the CPU usage.
         *
         * @param cpuUsage the CPU usage as a fraction between 0 and 1
         * @return this builder
         */
        public Builder cpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
            return this;
        }

        /**
         * Builds a new {@link Health} instance from the current builder state.
         *
         * @return a new health instance
         */
        public Health build() {
            return newHealth(this);
        }
    }
}
