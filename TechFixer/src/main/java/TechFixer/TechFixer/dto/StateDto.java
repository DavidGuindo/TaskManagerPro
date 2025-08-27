package TechFixer.TechFixer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateDto {

    private Long id;
    private String name;
    private List<Long> tasksID;

}
