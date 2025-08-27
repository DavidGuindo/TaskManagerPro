package TechFixer.TechFixer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDto {

    private Long id;
    private String description;
    private LocalDateTime date;
    private Long taskID;
    private Long authorID;
    private String authorName;
}
