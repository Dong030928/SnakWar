package com.kob.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 这段代码是一个用于配置WebSocket的类。它使用了Spring Framework中的注解，表示这是一个配置类，并且定义了一个名为WebSocketConfig的类。
 * 在该类中，使用了@Bean注解来声明一个名为serverEndpointExporter的方法。该方法返回一个ServerEndpointExporter对象。
 * ServerEndpointExporter是Spring提供的用于将带有@ServerEndpoint注解的类注册为WebSocket端点的类。它会自动扫描带有@ServerEndpoint注解的类，并将它们注册为WebSocket端点，以便能够处理WebSocket连接。
 * 通过在配置类中声明serverEndpointExporter方法并返回一个ServerEndpointExporter对象，Spring会自动为我们完成WebSocket端点的注册和配置工作。
 * 这样，我们就可以在其他地方使用@ServerEndpoint注解来创建WebSocket端点类，并且它们将会被自动注册和配置。
 */

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }
}