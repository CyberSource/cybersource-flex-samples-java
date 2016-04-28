/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
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
