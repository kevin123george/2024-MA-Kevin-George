package com.cattleDB.gate_way;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateWayApplication.class, args);
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("data-injection-service", r -> r
						.path("/data-injection/**")
						.uri("http://localhost:9091"))
				.route("watcher-pattern-service", r -> r
						.path("/watcher/**")
						.uri("http://localhost:9092"))
				.route("location-simulation-service", r -> r
						.path("/location/**")
						.uri("http://localhost:9094"))
				.route("front-end-service", r -> r
						.path("/frontend/**")
						.uri("http://localhost:9090"))
				.route("database-migration-service", r -> r
						.path("/db-migration/**")
						.uri("http://localhost:9095"))
				.route("metabase", r -> r
						.path("/metabase/**")
						.uri("http://localhost:3001"))
				.build();
	}
}
