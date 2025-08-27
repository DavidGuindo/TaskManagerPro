package TechFixer.TechFixer.controllers;

import TechFixer.TechFixer.dto.ProcessDto;
import TechFixer.TechFixer.services.ProcessService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/process")
@CrossOrigin(origins = "http://localhost:4200")
@Data
public class ProcessController {

    private final ProcessService processService;

    @PostMapping("/new")
    public ResponseEntity<?> newProcess(@RequestBody ProcessDto dto){
        try{
            processService.save(dto);
            return ResponseEntity.ok("Proceso creado correctamente");
         }catch (IllegalArgumentException | NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al crear proceso");
         }

    }

    @GetMapping("/getAll")
    public ResponseEntity<?> findAll(){
        try{
            return ResponseEntity.ok(processService.findAll());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al listar procesos");
        }
    }

    @GetMapping("/get/{processID}")
    public ResponseEntity<?> findByID(@PathVariable Long processID) {
        try {
            return ResponseEntity.ok(processService.findById(processID));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener proceso");
        }

    }


}
