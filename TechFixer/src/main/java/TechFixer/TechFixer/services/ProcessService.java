package TechFixer.TechFixer.services;

import TechFixer.TechFixer.dto.ProcessDto;
import TechFixer.TechFixer.entity.Process;
import TechFixer.TechFixer.mapper.ProcessMapper;
import TechFixer.TechFixer.repositories.ProcessRepository;
import TechFixer.TechFixer.repositories.TaskRepository;
import TechFixer.TechFixer.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ProcessService {

    private final ProcessRepository repo;
    private final UserRepository repoUser;
    private final TaskRepository repoTask;

    /**
     * Saves a new process entity based on the provided {@code ProcessDto}.
     * Validates that the author and task IDs exist in their respective repositories.
     * Populates the process entity fields with the provided and derived data,
     * and then persists it to the database.
     *
     * @param dto the {@code ProcessDto} object containing the data for the new process.
     *            Must include valid non-null {@code authorID} and {@code taskID}.
     * @throws IllegalArgumentException if {@code authorID} or {@code taskID} is null.
     * @throws NoSuchElementException if the {@code authorID} or {@code taskID} does not exist in their respective repositories.
     */
    public void save(ProcessDto dto){

        Process process = new Process();

        // Comporbamos que tenemos autor y que exista en BD, es obligatorio
        if(dto.getAuthorID() == null){ throw new IllegalArgumentException("El autor no puede estar vacio"); }
        else { process.setAuthor(
                repoUser.findById(dto.getAuthorID()).orElseThrow(() -> new NoSuchElementException("El autor " + dto.getAuthorID() + " no existe")));
        }

        // Comporbamos que tenemos tarea y que exista en BD, es obligatorio
        if(dto.getTaskID() == null){ throw new IllegalArgumentException("La tarea no puede estar vacio"); }
        else { process.setTask(
                repoTask.findById(dto.getTaskID()).orElseThrow(() -> new NoSuchElementException("La tarea " + dto.getTaskID() + " no existe")));
        }

        // Asignamos fecha y descripcion, siempre se asiganarán.
        process.setDate(LocalDateTime.now());
        process.setDescription(dto.getDescription());
        repo.save(process);
    }

    /**
     * Retrieves all processes and converts them into a list of {@code ProcessDto} objects.
     *
     * @return a list of {@code ProcessDto} objects representing all processes.
     */
    public List<ProcessDto> findAll(){
        return
                repo.findAll().stream().map(ProcessMapper::toDto).toList();
    }

    /**
     * Finds a process by its ID and converts it into a {@code ProcessDto}.
     *
     * @param id the ID of the process to be retrieved. Must not be null.
     * @return the {@code ProcessDto} representation of the process with the given ID.
     * @throws NoSuchElementException if no process is found with the specified ID.
     */
    public ProcessDto findById(Long id){
        return ProcessMapper.toDto(
                        repo.findById(id).orElseThrow(() -> new NoSuchElementException ("Proceso no encontrado"))
                );
    }


}
