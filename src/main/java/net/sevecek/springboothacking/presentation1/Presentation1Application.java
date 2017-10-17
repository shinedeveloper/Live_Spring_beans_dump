package net.sevecek.springboothacking.presentation1;

import java.io.*;
import java.security.*;
import java.util.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import net.sevecek.util.spring.contextdump.*;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Presentation1Application {

	public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext container =
                SpringApplication.run(Presentation1Application.class, args);
        Dumper dumper = container.getBean(Dumper.class);
        dumper.dumpBeans(System.out);
        System.in.read();
	}

	@Bean
    public Dumper dumper() {
        return new Dumper();
    }

	@Bean
    public Random randomGenerator() {
	    return new SecureRandom();
    }
}
