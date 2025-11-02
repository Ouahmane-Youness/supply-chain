package org.supplychain.supplychain.dto.BOM;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillOfMaterialDTO {

    private Long idBOM;

    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Long materialId;

    private String materialName;
    private String materialUnit;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

}
