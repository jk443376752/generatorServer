package com.tool4j.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class Tool4jGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tool4jGeneratorApplication.class, args);
        System.out.println("\n ==================  (♥◠‿◠)ﾉﾞ  Tool4J 启动成功   ლ(´ڡ`ლ)ﾞ  ================== \n");
    }

}
