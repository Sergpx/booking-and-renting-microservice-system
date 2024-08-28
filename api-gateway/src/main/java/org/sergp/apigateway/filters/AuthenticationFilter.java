package org.sergp.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.apigateway.dto.ValidationTokenResponse;
import org.sergp.apigateway.exceptions.AuthorizationException;
import org.sergp.apigateway.exceptions.BearerTokenException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public static final List<String> whiteListEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/authenticate",
            "/auth/activate-token",
            "/api/inventory",
            "/api/orders"
    );

    private String getBearerToken(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> authorizationHeaders = headers.get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            throw new AuthorizationException("Authorization header not found");
        }

        String authHeader = authorizationHeaders.get(0);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new BearerTokenException("Bearer token not found");
        }
        return authHeader.substring(7);


    }

    Predicate<ServerHttpRequest> isSecured =    // true if request is secured
            request -> whiteListEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            if(isSecured.test(exchange.getRequest())){
                String bearerToken = getBearerToken(exchange);
                return webClientBuilder.build().get()
                        .uri("lb://user-service/auth/validateToken")
                        .header("Authorization", bearerToken)
                        .retrieve()
                        .bodyToMono(ValidationTokenResponse.class)
                        .flatMap(response -> {
                            ServerHttpRequest request = exchange.getRequest().mutate()
                                    .header("username", response.getUsername())
                                    .header("id", response.getId())
                                    .header("authorities", String.join(",", response.getAuthorities()))
                                    .header("auth-token", response.getToken())
                                    .build();

                            return chain.filter(exchange.mutate().request(request).build());
                        })
                        .doOnError(error -> { log.error("Error while authenticating user", error);});

            }
            return chain.filter(exchange);
        };
    }
}
