package com.example.springaifunctions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.function.Function;

@SpringBootApplication
public class SpringAiFunctionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiFunctionsApplication.class, args);
    }

	@Bean
	@Description("Get the current status of your manager")
	public Function<MyManagerStatusService.Request, MyManagerStatusService.Response> getManagerStatus() {
		return new MyManagerStatusService();
	}

    @Bean
    RouterFunction<ServerResponse> routes(ChatClient.Builder chatClientBuilder) {
        return RouterFunctions.route()
                .GET("/", req -> {
					ChatClient chatClient = chatClientBuilder.build();
					String msg = req.param("msg").orElse("What's your name?");
					String answer = chatClient.prompt()
							.functions("getManagerStatus")
							.user(msg)
							.call()
							.content();

					return ServerResponse.ok().body(answer);
                }).build();
    }

}
