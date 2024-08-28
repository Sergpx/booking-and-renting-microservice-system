package org.sergp.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationTokenResponse {

    String id;
    String username;
    String token;
    List<String> authorities;

}
