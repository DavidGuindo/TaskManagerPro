package TechFixer.TechFixer.controllers;

import TechFixer.TechFixer.dto.FilterDto;
import TechFixer.TechFixer.dto.TaskDto;
import TechFixer.TechFixer.exception.NotFoundUserException;
import TechFixer.TechFixer.services.DepartmentService;
import TechFixer.TechFixer.services.TaskService;
import TechFixer.TechFixer.services.UserService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "http://localhost:4200")
@Data
public class TaskController {

    private final TaskService taskService;
    private final DepartmentService depService;
    private final UserService userService;

    /**
     * Handles the creation of a new task. The task details are provided in the form of
     * a TaskDto object, which includes properties such as task description, dates, and
     * associated user and department IDs. This method stores the task using the taskService.
     *
     * @param dataTask the TaskDto object containing the details of the task to be created,
     *                 including its description, dates, department, author, and owner.
     * @return a ResponseEntity containing a success message upon successful creation,
     *         or an error message if any exceptions occur during task creation.
     */
    @PostMapping("/new")
    public ResponseEntity<String> addTask(@RequestBody TaskDto dataTask){
        try {
            taskService.save(dataTask);
            return ResponseEntity.ok("Tarea creada");

        } catch (NoSuchElementException | NotFoundUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body( e.getMessage() );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al obtener tareas: " + e.getCause() + " --- " + e.getMessage());
        }
    }

    /**
     * Updates an existing task using the data provided in the TaskDto object. The task details
     * such as description, department, and owner are updated based on the provided information.
     * If the task, department, or owner does not exist, or if an error occurs during the process,
     * an appropriate error message is returned.
     *
     * @param dataTask the TaskDto object containing the updated details of the task. The object
     *                 should include the task ID, updated description, department ID, and owner ID.
     * @return a ResponseEntity containing:
     *         - A success message if the task is updated successfully.
     *         - An error message and conflict status if the task, department, or owner does not exist or
     *           if any other exception occurs during the update process.
     *         - An appropriate error message and conflict status for generic errors during updating.
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateTask(@RequestBody TaskDto dataTask){
        try {
            taskService.update(dataTask);
            return ResponseEntity.ok("Tarea actualizada");

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al actualizar tarea: " + e.getCause() + " --- " + e.getMessage());
        }
    }

    /**
     * Deletes a task from the system using the specified task ID.
     * If the task does not exist, an appropriate error message is returned.
     * In case of any unexpected issue, a generic error message is provided.
     *
     * @param taskId the unique identifier of the task to delete
     * @return a ResponseEntity containing:
     *         - A success message if the task is deleted successfully.
     *         - An error message and conflict status if the task does not exist or if an exception occurs during deletion.
     */
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId){
        try{
            taskService.delete(taskId);
            return ResponseEntity.ok("Tarea eliminada correctamente");

        }  catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al eliminar tarea");
        }
    }

    /**
     * Retrieves a task based on its unique identifier provided in the request path.
     * If the task exists, it is returned with an OK status.
     * If the task does not exist or an error occurs during retrieval,
     * an appropriate error message and status are returned.
     *
     * @param taskId the unique identifier of the task to retrieve
     * @return a ResponseEntity containing:
     *         - The retrieved task object if it exists, along with an OK status.
     *         - A NOT FOUND status and error message if the task does not exist.
     *         - A NOT FOUND status and a generic error message if any other exception occurs during retrieval.
     */
    @GetMapping("/get/{taskId}")
    public ResponseEntity<?> findeOne(@PathVariable Long taskId){
        try {
            return ResponseEntity.ok(taskService.findById(taskId));
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener tarea");
        }
    }

    /**
     * Retrieves all tasks available in the system. Internally, it invokes the
     * taskService to fetch all tasks and returns the results. In case of
     * an exception, an error message is returned with a NOT FOUND status.
     *
     * @return a ResponseEntity containing:
     *         - A list of all tasks if the retrieval is successful.
     *         - A NOT FOUND status and an error message if an exception occurs during retrieval.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> findAllTasks(){
        try{
            return ResponseEntity.ok(taskService.findAll());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener tareas: " + e.getCause() + " --- " + e.getMessage());
        }
    }

    /** Devuelve las tareas pendientes de un usuario (Activo, En proceso y Pausadas)*/
    @GetMapping("/getAllByUserPending/{userId}")
    public ResponseEntity<?> findAllTasksByUser(@PathVariable Long userId){
        try{
            List<TaskDto> tasks = taskService.findAllByUserPending(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("active", tasks.stream().filter(t -> t.getStateID() == 1L).toList());
            response.put("process", tasks.stream().filter(t -> t.getStateID() == 2L).toList());
            response.put("paused", tasks.stream().filter(t -> t.getStateID() == 3L).toList());

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener tareas: " + e.getCause() + " --- " + e.getMessage());
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<?> findFilter(@RequestBody FilterDto filterDto){

        try{
            return ResponseEntity.ok(taskService.findFilter(filterDto));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener tareas: " + e.getCause() + " --- " + e.getMessage());
        }

    }
}
