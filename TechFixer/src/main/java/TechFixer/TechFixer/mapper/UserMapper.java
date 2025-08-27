package TechFixer.TechFixer.mapper;

import TechFixer.TechFixer.dto.UserDto;
import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.Roles;
import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserDto toDto (User user){
        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setPassword(user.getPassword());
        dto.setRolName(user.getRol().getName());
        dto.setRolID(user.getRol().getId());

        // Si tenemos departamentos, las pasamos en caso contrario la ponemos a null
        if(!user.getDepartments().isEmpty()){
            dto.setDepartmentsID(
                    user.getDepartments()
                            .stream()
                            .map(Department::getId)
                            .toList());
        } else { dto.setDepartmentsID(null); }

        // Si tenemos tareas como Autor, las pasamos en caso contrario la ponemos a null
        if(!user.getAuthorTasks().isEmpty()){
            dto.setAuthorTasksID(
                    user.getAuthorTasks()
                            .stream()
                            .map(Task::getId)
                            .toList());
        } else { dto.setAuthorTasksID(null); }

        // Si tenemos tareas como Autor, las pasamos en caso contrario la ponemos a null
        if(!user.getOwnerTasks().isEmpty()){
            dto.setOwnerTasksID(
                    user.getOwnerTasks()
                            .stream()
                            .map(Task::getId)
                            .toList());
        } else { dto.setOwnerTasksID(null); }

        return dto;
    }

    public static User toUser (UserDto dto, List<Department> dpts, List<Task> authorTaskList, List<Task> ownerTaskList){
        User user = new User();

        user.setId(dto.getId());
        user.setPassword(dto.getPassword());
        user.setRol(new Roles(dto.getRol()));

        // Sino existen departamentos, los ponemos a null
        if(!dpts.isEmpty()){
           user.setDepartments(dpts);
        } else { user.setDepartments(new ArrayList<Department>()); }

        // Sino existen tareas autor, las ponemos a null
        if(!authorTaskList.isEmpty()){
            user.setAuthorTasks(authorTaskList);
        } else { user.setAuthorTasks(new ArrayList<Task>()); }

        // Sino existen tareas asociasdas, las ponemos a null
        if(!ownerTaskList.isEmpty()){
            user.setOwnerTasks(ownerTaskList);
        } else { user.setOwnerTasks(new ArrayList<Task>()); }

        return user;
    }
}
