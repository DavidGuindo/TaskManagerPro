package TechFixer.TechFixer.controllers;

import TechFixer.TechFixer.dto.DepartmentDto;
import TechFixer.TechFixer.exception.DepartmentAlreadyExistException;
import TechFixer.TechFixer.exception.NotFoundDepartmentException;
import TechFixer.TechFixer.services.DepartmentService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/department")
@CrossOrigin(origins = "http://localhost:4200")
@Data
public class DepartmentController {

    private final DepartmentService depService;

    /**
     * Creates a new department using the provided department data.
     * The department name must be unique, and users can be associated with the department.
     * Handles various exceptions for invalid input, duplicate department, and unexpected errors.
     *
     * @param dataDepartment the data transfer object containing the information for the new department.
     *                       Must include a non-empty name. If users are provided, their IDs must
     *                       reference existing users.
     * @return a {@code ResponseEntity<String>} containing a success message if the department is created,
     *         or an error message in case of a failure.
     *         Possible response statuses are:
     *         - {@code HttpStatus.OK}: Department created successfully.
     *         - {@code HttpStatus.CONFLICT}: Department creation failed due to conflict or other errors.
     */
    // Crea nuevo departamento
    @PostMapping("/new")
    public ResponseEntity<String> addDepartment(@RequestBody DepartmentDto dataDepartment){
        try {
            System.out.println("Vamos a crear un nuevo departamento con el nombre: " + dataDepartment);
            depService.save(dataDepartment);
            return ResponseEntity.ok("Departamento creado");

        } catch (DepartmentAlreadyExistException | IllegalArgumentException | UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al crear departamento");
        }
    }

    /**
     * Updates an existing department using the provided department data.
     * The update may include changes to the department name and associated users.
     * In case of missing or invalid data, an appropriate error response is returned.
     *
     * @param dataDepartment the data transfer object containing the updated department information.
     *                       Must include the department ID. If a new name is provided, it must not be null or empty.
     *                       If users are specified, their IDs must reference existing users.
     * @return a {@code ResponseEntity<String>} containing a success message if the department is updated successfully,
     *         or an error message in case of a failure. Possible response statuses are:
     *         - {@code HttpStatus.OK}: Department updated successfully.
     *         - {@code HttpStatus.CONFLICT}: Conflict occurred, e.g., invalid data or department/user not found.
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateDepartment(@RequestBody DepartmentDto dataDepartment){
        try {
            System.out.println("Vamos a actualizar departamento.");
            depService.update(dataDepartment);
            return ResponseEntity.ok("Departamento actualizado");

        } catch (NoSuchElementException | UsernameNotFoundException | DepartmentAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al actualizar departamento");
        }
    }

    /**
     * Deletes a department identified by its unique ID. If the department does not exist,
     * or if the department has associated users, an appropriate exception is thrown, returning
     * a conflict HTTP status. Otherwise, the department is successfully deleted.
     *
     * @param dptId the unique identifier of the department to be deleted. Must reference an existing department.
     * @return a {@code ResponseEntity} containing:
     *         - a success message if the department is deleted successfully with {@code HttpStatus.OK}.
     *         - an error message with {@code HttpStatus.CONFLICT} if deletion fails due to an exception.
     */
    @DeleteMapping("/delete/{dptId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long dptId){
        try{
            System.out.println("Vamos a borrar el departamento: " + dptId);
            depService.delete(dptId);
            return ResponseEntity.ok("Departamento eliminado correctamente");

        }  catch (NotFoundDepartmentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al eliminar departamento");
        }
    }

    /**
     * Retrieves the details of a specific department by its unique identifier.
     * If the department exists, it returns the department details; otherwise, it returns an error response.
     *
     * @param dptId the unique identifier of the department to retrieve. Must not be null.
     * @return a {@code ResponseEntity} containing the department details if found,
     *         or an error message with an appropriate HTTP status if the department is not found or an error occurs.
     */
    @GetMapping("/get/{dptId}")
    public ResponseEntity<?> findeOne(@PathVariable Long dptId){
        try {
            System.out.println("Obtenemos los datos del departamento: " + dptId);
            return ResponseEntity.ok(depService.findById(dptId));
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener departamento");
        }
    }

    /**
     * Retrieves a list of all departments.
     *
     * @return a {@code ResponseEntity} object containing a list of all departments if successful,
     *         or an error message with an appropriate HTTP status in case of failure.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> findAllDepartment(){
        try{
            System.out.println("Vamos a listar todos los departamentos: ");
            return ResponseEntity.ok(depService.findAll());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener departamentos");
        }
    }
}
