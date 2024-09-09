package org.sergp.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUser {

    @Pattern(regexp = "^(?!\\s)\\S.*$", message = "New name should not start with a space and should not consist of spaces only.")
    private String username;
    @Pattern(regexp = "^(?!\\s)\\S.*$", message = "New password should not start with a space and should not consist of spaces only.")
    private String password;
    @Email(message = "Invalid email address")
    private String email;


}
