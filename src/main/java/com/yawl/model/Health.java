package com.yawl.model;

public record Health(Status status, Long availableMemory, Long totalMemory, Long usedMemory, double cpuUsage) {

    private static Health newHealth(Builder builder) {
        return new Health(builder.status, builder.availableMemory, builder.totalMemory, builder.totalMemory - builder.availableMemory, builder.cpuUsage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum Status {
        UP(HttpStatus.OK),
        DOWN(HttpStatus.ERROR),
        STARTING(HttpStatus.ERROR);

        private final HttpStatus status;

        Status(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    public static class Builder {
        private Status status;
        private Long availableMemory;
        private Long totalMemory;
        private double cpuUsage;


        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder availableMemory(Long availableMemory) {
            this.availableMemory = availableMemory;
            return this;
        }

        public Builder totalMemory(Long totalMemory) {
            this.totalMemory = totalMemory;
            return this;
        }

        public Builder cpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
            return this;
        }

        public Health build() {
            return newHealth(this);
        }
    }
}
