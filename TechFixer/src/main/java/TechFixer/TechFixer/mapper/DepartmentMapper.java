package TechFixer.TechFixer.mapper;

import TechFixer.TechFixer.dto.DepartmentDto;
import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.entity.User;

import java.util.List;

public class DepartmentMapper {

    public static DepartmentDto toDto(Department dpt){
        DepartmentDto dto = new DepartmentDto();
        dto.setId(dpt.getId());
        dto.setName(dpt.getName());
        dto.setTasksID(dpt.getTasks().stream().map(Task::getId).toList());
        dto.setUsersID(dpt.getUsers().stream().map(User::getId).toList());

        return dto;
    }

    public static Department toDepartment(DepartmentDto dto, List<Task> tasks, List<User> users){
        Department dpt = new Department();
        dpt.setId(dto.getId());
        dpt.setName(dto.getName());
        dpt.setTasks(tasks);
        dpt.setUsers(users);

        return dpt;
    }


}
