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
        try {
            FlexKeyProvider fkp = new FlexKeyProvider();
            sce.getServletContext().setAttribute(FlexKeyProvider.class.getName(), fkp);
        } finally {
            
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
