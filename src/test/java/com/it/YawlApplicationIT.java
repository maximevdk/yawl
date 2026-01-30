package com.it;

import com.yawl.YawlApplication;
import com.yawl.annotations.EnableHttpClients;

@EnableHttpClients(classes = RestClient.class)
public class YawlApplicationIT {
    static void main(String[] args) {
        YawlApplication.run(YawlApplicationIT.class, args);
    }
}
