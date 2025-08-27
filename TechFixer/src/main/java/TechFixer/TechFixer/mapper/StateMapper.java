package TechFixer.TechFixer.mapper;

import TechFixer.TechFixer.dto.StateDto;
import TechFixer.TechFixer.entity.State;
import TechFixer.TechFixer.entity.Task;

import java.util.List;

public class StateMapper {

    public static StateDto toDto(State state){
        StateDto dto = new StateDto();

        dto.setId(state.getId());
        dto.setName(state.getName());
        dto.setTasksID(state.getTasks().stream().map(Task::getId).toList());
        return dto;
    }

    public static State toState(StateDto dto, List<Task> tasks){
        State state = new State();

        state.setId(dto.getId());
        state.setName(dto.getName());
        state.setTasks(tasks);

        return state;
    }


}
