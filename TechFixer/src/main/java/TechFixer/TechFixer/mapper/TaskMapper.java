package TechFixer.TechFixer.mapper;

import TechFixer.TechFixer.dto.TaskDto;
import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.Process;
import TechFixer.TechFixer.entity.State;
import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.entity.User;

import java.util.List;

public class TaskMapper {


    public static TaskDto toDto(Task task){
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setDateIni(task.getDateIni());
        dto.setDateEnd(task.getDateEnd());
        dto.setDescription(task.getDescription());
        if(task.getDpt() != null){
            dto.setDptID(task.getDpt().getId());
            dto.setDptName(task.getDpt().getName());
        }
        if (task.getAuthor() != null){
            dto.setAuthorID(task.getAuthor().getId());
            dto.setAuthorName(task.getAuthor().getUserName());
        }
        if (task.getOwnerUser() != null){
            dto.setOwnerID(task.getOwnerUser().getId());
            dto.setOwnerName(task.getOwnerUser().getUserName());
        }
        if (task.getState() != null) {
            dto.setStateID(task.getState().getId());
            dto.setStateName(task.getState().getName());
        }
        if(task.getProcesses() != null){
            dto.setProcessDtos(task.getProcesses().stream().map(ProcessMapper::toDto).toList());
        }
        return dto;
    }

    public static Task toTask(TaskDto dto, Department dpt, User author, User owner, State state, List<Process> process){
        Task task = new Task();
        task.setId(dto.getId());
        task.setDateIni(dto.getDateIni());
        task.setDateEnd(dto.getDateEnd());
        task.setDescription(dto.getDescription());
        task.setDpt(dpt);
        task.setAuthor(author);
        task.setOwnerUser(owner);
        task.setState(state);
        task.setProcesses(process);

        return task;
    }



}
