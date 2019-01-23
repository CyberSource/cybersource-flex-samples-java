/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FlexServletContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        InputStream configurationProperties = null;
        try {
            configurationProperties = sce.getServletContext().getResourceAsStream("/WEB-INF/credentials.properties");
            FlexKeyProvider fkp = new FlexKeyProvider(configurationProperties);
            sce.getServletContext().setAttribute(FlexKeyProvider.class.getName(), fkp);
        } finally {
            if (configurationProperties != null) {
                try {
                    configurationProperties.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
