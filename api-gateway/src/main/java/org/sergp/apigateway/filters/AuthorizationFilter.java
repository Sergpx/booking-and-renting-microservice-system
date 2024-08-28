package org.sergp.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.apigateway.exceptions.ForbiddenException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;




@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig>{

    private final ObjectMapper objectMapper;

    public static final List<String> adminListEndpoints = List.of(
            "/property/admin"
    );


    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(adminListEndpoints.stream().anyMatch(request.getURI().getPath()::startsWith)){
                if(!isAdmin(exchange)){
                    throw new ForbiddenException("Access denied");
                }
            }
            return chain.filter(exchange);
        };
    }

    private boolean isAdmin(ServerWebExchange exchange) {
        List<String> authorities = exchange.getRequest().getHeaders().get("authorities");
        return (authorities != null && authorities.contains("ROLE_ADMIN"));
    }



}
