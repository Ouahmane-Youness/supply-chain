package org.supplychain.supplychain.dto.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class SupplierDTO {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit avoir entre 2 et 100 caractères")
    private String name;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Le téléphone doit contenir uniquement des chiffres (8-15 chiffres)")
    private String phone;

    @NotNull(message = "La note (rating) est obligatoire")
    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 5, message = "La note maximale est 5")
    private Double rating;

    @NotNull(message = "Le délai (leadTime) est obligatoire")
    @Min(value = 1, message = "Le délai doit être au moins 1 jour")
    private Integer leadTime;

    @NotNull(message = "La liste des matériaux est obligatoire")
    @Size(min = 1, message = "Au moins un matériau doit être sélectionné")
    private List<Long> materialIds;
}
