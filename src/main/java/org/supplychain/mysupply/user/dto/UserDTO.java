package org.supplychain.mysupply.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.user.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long idUser;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is Required")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

}
