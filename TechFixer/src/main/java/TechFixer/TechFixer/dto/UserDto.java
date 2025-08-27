package TechFixer.TechFixer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String userName;
    private String password;
    private String rol;
    private Long rolID;
    private String rolName;
    private List<Long> departmentsID;
    private List<Long> authorTasksID;
    private List<Long> ownerTasksID;
}
