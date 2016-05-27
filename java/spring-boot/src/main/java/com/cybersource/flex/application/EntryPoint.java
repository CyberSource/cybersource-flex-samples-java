/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Field;

@SpringBootApplication
public class EntryPoint {

    public static void main(String... args) throws Exception {
        SpringApplication.run(EntryPoint.class, args);
    }

}
