package com.yawl.configuration;


import com.yawl.ManagementServlet;
import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.beans.model.CommonBeans;

@Configuration(condition = @Configuration.Condition(property = "application.management.endpoint.enabled", value = "true"))
public class ManagementServletConfiguration {

    @Bean(name = CommonBeans.MANAGEMENT_SERVLET_NAME)
    public ManagementServlet managementServlet() {
        return new ManagementServlet();
    }
}
