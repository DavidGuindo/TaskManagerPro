package TechFixer.TechFixer.services;


import TechFixer.TechFixer.dto.FilterDto;
import TechFixer.TechFixer.dto.TaskDto;
import TechFixer.TechFixer.entity.Process;
import TechFixer.TechFixer.entity.Task;
import TechFixer.TechFixer.exception.NotFoundUserException;
import TechFixer.TechFixer.mapper.TaskMapper;
import TechFixer.TechFixer.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.*;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository repo;
    private final DepartmentRepository repoDtp;
    private final UserRepository repoUser;
    private final StateRepository repoState;
    private final ProcessRepository repoProcess;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a new task to the repository. The method initializes a Task entity
     * based on the provided TaskDto, sets its properties including its department, author,
     * owner, state, and description, and then saves it. Additionally, any associated processes
     * defined in the TaskDto are processed and saved for the created task.
     *
     * @param taskDto the data transfer object containing the details of the task to be created,
     *                including its department ID, author ID, owner ID, state ID, description,
     *                and a list of processes.
     * @throws NoSuchElementException if any provided ID (department, author, owner, state, or process author)
     *                                does not correspond to an existing entity in the repository.
     * @throws NotFoundUserException if the author ID is not provided in the TaskDto, as the author
     *                               field is mandatory.
     */
    public void save(TaskDto taskDto) {
        Task task = new Task();

        // Obtenemos el departamento, sino hay lo ponemos nulo
        if (taskDto.getDptID() != null) {
            task.setDpt(repoDtp.findById(taskDto.getDptID()).orElseThrow(() -> new NoSuchElementException("Departamento no encontrado")));
        } else { task.setDpt(null); }

        // Obtenemos el autor y sino hay, se lanza excepcion. El autor es obligatorio
        if (taskDto.getAuthorID() != null) {
            task.setAuthor(repoUser.findById(taskDto.getAuthorID()).orElseThrow(() -> new NoSuchElementException("Autor no encontrado")));
        } else { throw new NotFoundUserException("Autor obligatorio"); }

        // Obtenemos el usuario asociado, sino hay a nulo
        if (taskDto.getOwnerID() != null) {
            task.setOwnerUser(repoUser.findById(taskDto.getOwnerID()).orElseThrow(() -> new NoSuchElementException("Usuarios encargado no encontrado")));
        }  else { task.setOwnerUser(null); }

        // Obtenemos el estatado, sino hay lo ponemos Activo por defecto
        if(taskDto.getStateID() != null) {
            task.setState(repoState.findById(taskDto.getStateID()).orElseThrow(() -> new NoSuchElementException("Estado no encontrado")));
        } else { task.setState(repoState.findById(1L).orElseThrow(() -> new NoSuchElementException("Estado no encontrado"))); }

        // Asignamos descripcion y fecha de creacion
        task.setDescription(taskDto.getDescription());
        task.setDateIni(LocalDateTime.now());

        repo.save(task);

        // Comprobamos si tiene procesos, esto se tiene que hacer despues de crear la tarea para que no haya problemas de referecias
        if(taskDto.getProcessDtos() != null){
            taskDto.getProcessDtos().forEach(dto -> {
                Process process = new Process();
                process.setDescription(dto.getDescription());
                process.setDate(LocalDateTime.now());
                process.setAuthor(task.getAuthor());
                process.setTask(task);
                repoProcess.save(process);

            });
        }

    }

    /**
     * Updates an existing task in the repository with the information provided in the TaskDto.
     * This method verifies the existence of the task and updates its department, owner, and description
     * based on the provided data. If a field in the DTO is null, it resets the associated field in the task.
     *
     * @param taskDto the data transfer object containing updated information about the task,
     *            including its ID, department ID, owner ID, and description.
     * @throws NoSuchElementException if the task, specified department, or owner does not exist.
     */
    public void update(TaskDto taskDto){
        Task dbTask = repo.findById(taskDto.getId()).orElseThrow(() -> new NoSuchElementException("La tarea no existe"));

        // Si la tarea tiene estado finalizado no se puede modificar por lo que lanzamos excepcion
        if(dbTask.getState().getId().equals(4L)){
            throw new IllegalStateException("La tarea ya ha sido finalizada, no admite modificaciones");
        }

        // ¿Hay que cambiar el departamento? Si nos lo dan lo buscamos, sino significa que le han quitado el dpt
        if(taskDto.getDptID() != null){
            dbTask.setDpt(
                    repoDtp.findById(taskDto.getDptID()).orElseThrow(() -> new NoSuchElementException("Departamento no existe"))
            );
        } else { dbTask.setDpt(null); }

        // ¿Hay que cambiar el usuario asociado? Si nos lo dan lo buscamos, sino significa que le han quitado el usuario asociado
        if(taskDto.getOwnerID() != null){
            dbTask.setOwnerUser(
                    repoUser.findById(taskDto.getOwnerID()).orElseThrow(() -> new NoSuchElementException("Usuario no existe"))
            );
        } else { dbTask.setOwnerUser(null); }

        // ¿Hay que cambiar el estado? En caso de cambiarlo, primero comprobamos que exista, sino excepcion
        if(taskDto.getStateID() != null && !taskDto.getStateID().equals(dbTask.getState().getId()) ){
            dbTask.setState(
                repoState.findById(taskDto.getStateID()).orElseThrow(() -> new NoSuchElementException("Estado no existe"))
            );

            // Si el estado es finalizado, asignamos una fecha de fin
            if(dbTask.getState().getId().equals(4L)){
                dbTask.setDateEnd(LocalDateTime.now());
            }
        }

        // ¿Hay que cambiar la descripcion?
        if(dbTask.getDescription() != null && !dbTask.getDescription().equals(taskDto.getDescription()) ){
            dbTask.setDescription(taskDto.getDescription());
        }

        repo.save(dbTask);

        // ¿Hay que añadir procesos?
        if(taskDto.getProcessDtos() != null){
            taskDto.getProcessDtos().forEach(processDto -> {
                Process process = new Process();
                process.setDescription(processDto.getDescription());
                process.setDate(LocalDateTime.now());
                process.setAuthor(repoUser.findById(processDto.getAuthorID()).orElseThrow(() -> new NoSuchElementException("El autor del proceso no existe")));
                process.setTask(dbTask);
                repoProcess.save(process);
            });
        }


    }

    /**
     * Deletes a task from the database based on its unique identifier.
     * If the task with the specified ID does not exist, a NoSuchElementException is thrown.
     *
     * @param id the unique identifier of the task to delete
     * @throws NoSuchElementException if no task exists with the specified ID
     */
    @Transactional
    public void delete(Long id){
        Task toDelete = repo.findById(id).orElseThrow(() -> new NoSuchElementException("La tarea no existe"));
        repo.delete(toDelete);
    }

    /**
     * Retrieves a task by its unique identifier and maps it to a TaskDto object.
     * If the task with the specified ID does not exist, a NoSuchElementException is thrown.
     *
     * @param id the unique identifier of the task to retrieve
     * @return a TaskDto object representing the retrieved task
     * @throws NoSuchElementException if no task exists with the specified ID
     */
    public TaskDto findById(Long id){
        return TaskMapper.toDto(
                repo.findById(id).orElseThrow(() -> new NoSuchElementException("La tarea no existe"))
        );
    }

    /**
     * Retrieves all tasks from the repository, maps them to TaskDto objects using
     * the TaskMapper, and returns the list of mapped TaskDto objects.
     *
     * @return a list of TaskDto objects representing all tasks in the repository
     */
    public List<TaskDto> findAll(){
        return
                repo.findAll().stream().map(TaskMapper::toDto).toList();

    }

    public List<TaskDto> findAllByUserPending(Long userID){
        // Comprobamos que el usuario existe
        if(repoUser.findById(userID).isEmpty()){throw new NoSuchElementException("El usuario no existe");}

        return  repo.findByOwnerUser_IdAndState_IdInOrderByDateIniDesc(userID, List.of(1L,2L,3L)).stream().map(TaskMapper::toDto).toList();
    }

    public List<TaskDto> findFilter(FilterDto filter){

        Map<String, Object> params = new HashMap<>();
        StringBuilder query = new StringBuilder("SELECT t FROM Task t WHERE 1=1 ");

        if(filter != null && !filter.equals(new FilterDto())) {
            // ¿Filtramos por autor?
            if (filter.getAuthorID() != null) {
                query.append( " AND t.author.id = :authorID ");
                params.put("authorID", filter.getAuthorID());
            }

            // Filtramos por usuarios asociado?
            if (filter.getOwnerID() != null) {
                query.append(" AND t.ownerUser.id = :ownerID ");
                params.put("ownerID", filter.getOwnerID());
            }

            // ¿Filtramos por estado?
            if (filter.getStateID() != null) {
                query.append(" AND t.state.id = :stateID ");
                params.put("stateID", filter.getStateID());
            }

            // Filtramos por departamento?
            if (filter.getDepartmentID() != null) {
                query.append(" AND t.dpt.id = :departmentID ");
                params.put("departmentID", filter.getDepartmentID());
            }

            //¿Filtramos por fecha de creacion?
            if (filter.getDateCreationIni() != null && filter.getDateCreationEnd() != null) {
                query.append(" AND t.dateIni BETWEEN :dateCreationIni AND :dateCreationEnd ");
                params.put("dateCreationIni", filter.getDateCreationIni());
                params.put("dateCreationEnd", filter.getDateCreationEnd());

            // Solo fecha de inicio
            } else if (filter.getDateCreationIni() != null) {
                query.append(" AND t.dateIni >= :dateCreationIni ");
                params.put("dateCreationIni", filter.getDateCreationIni());

            // Solo fecha de fin
            } else if (filter.getDateCreationEnd() != null) {
                query.append(" AND t.dateIni <= :dateCreationEnd ");
                params.put("dateCreationEnd", filter.getDateCreationEnd());
            }

            //¿Filtramos por fecha de finalizacion?
            if (filter.getDateEndingIni() != null && filter.getDateEndingEnd() != null) {
                query.append(" AND t.dateEnd BETWEEN :dateEndingIni AND :dateEndingEnd ");
                params.put("dateEndingIni", filter.getDateEndingIni());
                params.put("dateEndingEnd", filter.getDateEndingEnd());

            // Solo fecha inicio
            } else if (filter.getDateEndingIni() != null) {
                query.append(" AND t.dateEnd >= :dateEndingIni ");
                params.put("dateEndingIni", filter.getDateEndingIni());

            // Solo fecha de fin
            } else if (filter.getDateEndingEnd() != null) {
                query.append(" AND t.dateEnd <= :dateEndingEnd ");
                params.put("dateEndingEnd", filter.getDateEndingEnd());
            }
        }

        TypedQuery<Task> query1 = entityManager.createQuery(query.toString(), Task.class);
        params.forEach(query1::setParameter);

        return query1.getResultList().stream().map(TaskMapper::toDto).toList() ;
    }


}
