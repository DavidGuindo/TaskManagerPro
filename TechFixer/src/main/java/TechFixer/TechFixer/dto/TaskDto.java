package TechFixer.TechFixer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;
    private LocalDateTime dateIni;
    private LocalDateTime dateEnd;
    private String description;
    private Long dptID;  private String dptName;
    private Long authorID; private String authorName;
    private Long ownerID; private String ownerName;
    private Long stateID; private String stateName;
    private List<ProcessDto> processDtos;
}
