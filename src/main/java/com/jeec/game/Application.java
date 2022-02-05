/*
 * Decompiled with CFR 0.137.
 *
 * Could not load the following classes:
 *  org.springframework.boot.CommandLineRunner
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 */
package com.jeec.game;

import com.jeec.game.GameController;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages={"com.jeec.game", "com.jeec.pong"})
@EnableSwagger2
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("CatanHelper started.");
            String ip = GameController.getIp();
            String url = "http://" + ip + ":8080";
            if (args.length>0 && args[0].equalsIgnoreCase("dixittest")) {
                System.out.println("Arguments:" + args.length + " : " + args[0]);
                GameController.setTestMode(true);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                }
                catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url );
                }
                catch (IOException e) {
                    System.out.println("Cannot open browser with page.");
                    //e.printStackTrace();
                }
            }
            System.out.println("Open this page on your mobile device to join game:\n http://" + ip + ":8080/start.html");
        };
    }

    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
           .apis(RequestHandlerSelectors.basePackage("com.jeec.game")).build();
     }
}
