package TechFixer.TechFixer.mapper;

import TechFixer.TechFixer.dto.ProcessDto;
import TechFixer.TechFixer.entity.Process;
import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.entity.User;

public class ProcessMapper {

    public static ProcessDto toDto(Process process){
        ProcessDto dto = new ProcessDto();

        dto.setId(process.getId());
        dto.setDescription(process.getDescription());
        dto.setDate(process.getDate());
        dto.setTaskID(process.getTask().getId());
        dto.setAuthorID(process.getAuthor().getId());
        dto.setAuthorName(process.getAuthor().getUserName());

        return dto;
    }

    public static Process toProcess(ProcessDto dto, User author, Task task){
        Process process = new Process();

        process.setId(dto.getId());
        process.setDescription(dto.getDescription());
        process.setDate(dto.getDate());
        process.setAuthor(author);
        process.setTask(task);

        return process;
    }
}
