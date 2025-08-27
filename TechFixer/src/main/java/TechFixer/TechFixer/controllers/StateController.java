package TechFixer.TechFixer.controllers;

import TechFixer.TechFixer.dto.StateDto;
import TechFixer.TechFixer.entity.State;
import TechFixer.TechFixer.exception.NotFoundStateException;
import TechFixer.TechFixer.exception.StateAlreadyExistException;
import TechFixer.TechFixer.services.StateService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/state")
@CrossOrigin(origins = "http://localhost:4200")
@Data
public class StateController {

    private final StateService stateService;

    /**
     * Adds a new state to the system using the provided state data.
     * If the state is successfully created, a success message is returned.
     * If the state already exists, or the name is invalid, an appropriate error message is returned.
     *
     * @param dtoState the data transfer object containing the state's details
     * @return a ResponseEntity containing:
     *         - A success message with an HTTP OK status if the state is successfully added.
     *         - An error message with an HTTP CONFLICT status if the state already exists,
     *           the name is invalid, or any other exception occurs during the operation.
     */
    @PostMapping("/new")
    public ResponseEntity<String> addState(@RequestBody StateDto dtoState){
        try {
            stateService.save(dtoState.getName());
            return ResponseEntity.ok("Estado creado");

        } catch (StateAlreadyExistException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al crear estado");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateState(@RequestBody StateDto dtoState){
        try {
            /**stateService.update(dtoState);*/
            return ResponseEntity.ok("Estado creado");

        } catch (StateAlreadyExistException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al crear estado");
        }
    }


    /**
     * Deletes a state based on the unique identifier provided in the request path.
     * If the state exists, it will be removed, and a success message will be returned.
     * In case of an error (e.g., the state does not exist or an invalid identifier),
     * an appropriate error response is returned.
     *
     * @param stateID the unique identifier of the state to delete
     * @return a ResponseEntity containing:
     *         - A success message with an OK status if the state is successfully deleted.
     *         - A conflict status and an error message if the state does not exist, the identifier is invalid,
     *           or any other exception occurs during the operation.
     */
    @DeleteMapping("/delete/{stateID}")
    public ResponseEntity<?> deleteState(@PathVariable Long stateID){

        try{
            stateService.delete(stateID);
            return ResponseEntity.ok("Estado eliminado correctamente");

        }  catch (NotFoundStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al eliminar estado");
        }
    }

    /**
     * Retrieves a list of all states from the system. This method leverages
     * the stateService to fetch all states and returns the result wrapped
     * in a ResponseEntity object. In case of an error during the retrieval
     * process, an appropriate error message is returned with a conflict status.
     *
     * @return a ResponseEntity containing:
     *         - A list of all states if the operation is successful, with an OK status.
     *         - A conflict status and an error message if an exception occurs during the process.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> findAllState(){
        try{
            return ResponseEntity.ok(stateService.getAll());

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al obtener estados");

        }

    }

    /**
     * Retrieves a state based on its unique identifier provided in the request path.
     * If the state exists, it is returned with an OK status. If the state does not exist,
     * or if an error occurs during retrieval, an appropriate error response is returned.
     *
     * @param stateID the unique identifier of the state to retrieve
     * @return a ResponseEntity containing:
     *         - The retrieved state object with an OK status if the state exists.
     *         - A NOT FOUND status with an error message if the state does not exist.
     *         - A NOT FOUND status with a generic error message if any other exception occurs during retrieval.
     */
    @GetMapping("/get/{stateID}")
    public ResponseEntity<?> findeOne(@PathVariable Long stateID){
        try {

            return ResponseEntity.ok(stateService.getById(stateID));

        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener estado");
        }


    }


}
