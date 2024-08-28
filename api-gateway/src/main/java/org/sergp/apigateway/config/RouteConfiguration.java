package org.sergp.apigateway.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sergp.apigateway.filters.AuthenticationFilter;
import org.sergp.apigateway.filters.AuthorizationFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class RouteConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        JsonFactory factory = new JsonFactory();
        factory.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

        ObjectMapper objectMapper = new ObjectMapper(factory);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper;
    }


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               AuthenticationFilter authenticationFilter,
                               AuthorizationFilter authorizationFilter) {

        return builder.routes()
                .route("property-service-route", r -> r.path("/properties/**")
                        .filters(f -> f
                                .filter(authenticationFilter.apply(new AbstractGatewayFilterFactory.NameConfig())))
                        .uri("lb://property-service"))

                .route("user-service-route", r -> r.path("/auth/**")
//                        .filters(f -> f
//                                .filter(authenticationFilter.apply(new AbstractGatewayFilterFactory.NameConfig()))) // TODO remove
                        .uri("lb://user-service"))


                .build();
    }

}
