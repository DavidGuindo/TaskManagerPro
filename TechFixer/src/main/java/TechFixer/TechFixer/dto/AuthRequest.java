package TechFixer.TechFixer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AuthRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;

    // Opcional, solo cuando se crea un usuario desde el panel de admin.
    private ArrayList<Long> departmentsID;
    private Long rolID;
}
