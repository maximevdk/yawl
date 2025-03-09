package com.yawl;

import java.io.File;

public record ApplicationProperties(Application application) {


    record Application(String name, WebConfiguration webConfig) {
        public String basePath() {
            return new File(".").getAbsolutePath();
        }
    }

    record WebConfiguration(int port, String contextPath) {
    }
}
