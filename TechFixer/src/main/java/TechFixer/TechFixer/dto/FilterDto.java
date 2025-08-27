package TechFixer.TechFixer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

    private Long authorID;
    private Long ownerID;
    private Long stateID;
    private Long departmentID;
    private LocalDateTime dateCreationIni;
    private LocalDateTime dateCreationEnd;
    private LocalDateTime dateEndingIni;
    private LocalDateTime dateEndingEnd;
}
