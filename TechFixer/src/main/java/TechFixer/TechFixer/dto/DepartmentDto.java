package TechFixer.TechFixer.dto;

import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {

    private Long id;
    private String name;
    private List<Long> usersID;
    private List<Long> tasksID;




}
