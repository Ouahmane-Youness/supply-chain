package org.supplychain.mysupply.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.user.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long idUser;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

}
