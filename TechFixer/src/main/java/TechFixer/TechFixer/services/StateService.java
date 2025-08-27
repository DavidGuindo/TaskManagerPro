package TechFixer.TechFixer.services;

import TechFixer.TechFixer.dto.StateDto;
import TechFixer.TechFixer.entity.State;
import TechFixer.TechFixer.exception.NotFoundStateException;
import TechFixer.TechFixer.exception.StateAlreadyExistException;
import TechFixer.TechFixer.mapper.StateMapper;
import TechFixer.TechFixer.repositories.StateRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StateService {

    private final StateRepository repo;

    /**
     * Initializes the default state values in the repository if the repository is empty.
     * This method is executed automatically after the bean's initialization, using the
     * @PostConstruct annotation. It checks if the repository contains any records, and
     * if it is empty, it populates it with a predefined list of state objects.
     *
     * The default states added are:
     * - Activo
     * - En proceso
     * - Pausado
     * - Finalizado
     * - Anulado
     */
    @PostConstruct
    public void iniValues(){
        if(repo.count() == 0){
            repo.saveAll(List.of(
                    new State("Activo"),
                    new State("En proceso"),
                    new State("Pausado"),
                    new State("Finalizado"),
                    new State("Anulado")
            ));
        }
    }


    /**
     * Saves a new state with the specified name. If the state name already exists in the repository,
     * a {@code StateAlreadyExistException} is thrown. If the provided name is {@code null}, an
     * {@code IllegalArgumentException} is thrown.
     *
     * @param name the name of the state to be saved
     * @throws StateAlreadyExistException if a state with the same name already exists
     * @throws IllegalArgumentException if the specified name is {@code null}
     */
    public void save(String name){

        // Sin nombre, error
        if (name == null) throw new IllegalArgumentException("El nombre del estado no puede estar vacio");
        // Si ya existe el nombre, error
        if (repo.findByName(name).isPresent()) throw new StateAlreadyExistException("El estado ya existe");
        repo.save(new State(name));

    }

    /**
     * Deletes a state from the repository based on its unique identifier. If the state with
     * the specified ID does not exist, a {@code NotFoundStateException} is thrown. If the
     * ID is null, an {@code IllegalArgumentException} is thrown.
     *
     * @param id the unique identifier of the state to delete
     * @throws NotFoundStateException if no state exists with the specified ID
     * @throws IllegalArgumentException if the specified ID is null
     */
    @Transactional
    public void delete(Long id){
        if (id == null) throw new IllegalArgumentException("El ID del estado no puede estar vacio");
        repo.delete(
            repo.findById(id)
                .orElseThrow(() -> new NotFoundStateException("Estado no encontrado"))
        );
    }

    /**
     * Retrieves all states from the repository, maps them to StateDto objects using the StateMapper,
     * and returns the list of mapped StateDto objects.
     *
     * @return a list of StateDto objects representing all states in the repository
     */
    public List<StateDto> getAll(){
        return repo.findAll().stream().map(StateMapper::toDto).toList();
    }

    /**
     * Retrieves a state based on its unique identifier and maps it to a {@code StateDto} object.
     * If no state exists with the specified ID, a {@code NoSuchElementException} is thrown.
     *
     * @param id the unique identifier of the state to retrieve
     * @return a {@code StateDto} object representing the retrieved state
     * @throws NoSuchElementException if no state exists with the specified ID
     */
    public StateDto getById(Long id){
        return StateMapper.toDto(
                repo.findById(id).orElseThrow(() -> new NoSuchElementException ("Estado no encontrado"))
        );
    }

}
