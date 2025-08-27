package TechFixer.TechFixer.services;

import TechFixer.TechFixer.dto.DepartmentDto;
import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.User;
import TechFixer.TechFixer.exception.DepartmentAlreadyExistException;
import TechFixer.TechFixer.exception.NotFoundDepartmentException;
import TechFixer.TechFixer.mapper.DepartmentMapper;
import TechFixer.TechFixer.repositories.DepartmentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class DepartmentService {

    private final DepartmentRepository repo;
    private final UserService userService;

    /**
     * Initializes default departments in the database if no departments currently exist.
     * This method is automatically called after the bean's properties have been initialized.
     * It ensures that a predefined set of departments is available in the system.
     * The predefined departments are:
     * - Tecnico
     * - Desarrollo
     * - Comercial
     * If the repository already contains departments, no action is taken.
     */
    @PostConstruct
    public void iniValues(){
        if(repo.count() == 0 ){
            repo.saveAll(List.of(
                    new Department ("Tecnico"),
                    new Department("Desarrollo"),
                    new Department("Comercial")
            ));
        }
    }

    /**
     * Saves a new department based on the provided {@code DepartmentDto}.
     * Ensures the department name is unique and associates users with the department.
     *
     * @param dto the data transfer object containing the information for the new department.
     *            Must include a non-empty name. If users are provided, their IDs must reference existing users.
     * @throws IllegalArgumentException if the department name is null or empty.
     * @throws DepartmentAlreadyExistException if a department with the same name already exists.
     * @throws NoSuchElementException if any user ID in the provided list does not reference an existing user.
     */
    public void save(DepartmentDto dto){

        // Sino tenemos nombre, excepcion
        if (dto.getName() == null) throw new IllegalArgumentException("El nombre del departamento no puede estar vacio");

        // Si el departamento ya existe, excepcion
        if (repo.findByName(dto.getName()).isPresent()) throw new DepartmentAlreadyExistException("El departamento ya exsite");

        // Si el id que nos ha llegado es 0, lo seteamos a Null para poder crear el objeto
        if(dto.getId() == 0) dto.setId(null);

        // Generamos el departamento a raiz del dto.
        Department newDep = DepartmentMapper.toDepartment(dto, null, null);

        repo.save(newDep);

        // Una vez creado el departamento, le asignamos a los usuarios el departamento
        // // Si algun usuarios no existe, saltará excepcion.
        if(dto.getUsersID() != null && !dto.getUsersID().isEmpty()) {
            dto.getUsersID().forEach(userID -> {
                userService.addDepartment(userID, newDep.getId());
            });
        }
    }

    /**
     * Updates an existing department based on the data provided in the {@code DepartmentDto}.
     * The update can include changes to the department name and associated users.
     * Throws an exception if the department or any associated user does not exist.
     *
     * @param dto the data transfer object containing the updated department information.
     *            Must include the department ID. If a new name is provided, it must not be null or empty.
     *            If users are specified, their IDs must reference existing users.
     * @throws NoSuchElementException if the department with the given ID does not exist.
     * @throws NoSuchElementException if any user ID in the provided list does not reference an existing user.
     */
    public void update(DepartmentDto dto){

        // Obtenemos los datos del departamento de la base de datos
        Department dptBd = repo.findById(dto.getId()).orElseThrow(() -> new NoSuchElementException("El departamento no existe"));

        // Comprbamos si hay que cambiar el nombre
        if( dto.getName() != null && !dto.getName().isEmpty() && !dto.getName().equals(dptBd.getName())){
            // Comprobamos que no haya otro departamento con el nombre que le queremos dar
            if (repo.findByName(dto.getName()).isPresent()) throw new DepartmentAlreadyExistException("El departamento ya exsite");

            dptBd.setName(dto.getName());
        }

        // Comprobamos si hay que cambiar usuarios
        if(dto.getUsersID() != null){

            // Obtenemos los usuarios del dto, sin alguno no exsite saltara excepcion
            List<User> listUsers = dto.getUsersID().stream().map(userService::findById).toList();

            // Comparamos los usuarios de bd con los que nos han pasado para obtener los nuevos usuarios
            List<User> toAdd = new ArrayList<>(listUsers);
            toAdd.removeAll(dptBd.getUsers());
            System.out.println("USUARIOS A AÑADIR: " + toAdd);

            toAdd.forEach(user -> { userService.addDepartment(user.getId(), dptBd.getId()); });
            System.out.println("USUARIOS añadidos.");


            // Ahora comparamos de nuevo los usuarios para obtener los que ya no formarán parte
            List<User> toDelete = new ArrayList<>(dptBd.getUsers());
            toDelete.removeAll(listUsers);
            System.out.println("USUARIOS A ELIMINAR: " + toDelete);

            toDelete.forEach(user -> { userService.removeDepartment(user.getId(), dptBd.getId()); });
            System.out.println("Usuarios eliminados");
        }

        repo.save(dptBd);
    }

    /**
     * Deletes a department by its ID. If the department does not exist, a NotFoundDepartmentException is thrown.
     * Additionally, deletion is disallowed if there are users associated with the department.
     *
     * @param id the unique identifier of the department to be deleted. Must reference an existing department.
     * @throws NotFoundDepartmentException if the department does not exist or if it has associated users.
     */
    @Transactional
    public void delete(Long id){
        // Comprobamos si existe el departamente, sino devovlemos una excepcion
        Department delDep = repo.findById(id).orElseThrow(() -> new NotFoundDepartmentException("El departamento no existe"));

        // Si el departamento tiene usuaios, no permitimos que se borre.
        if(!delDep.getUsers().isEmpty()) { throw new NotFoundDepartmentException("Error al eliminar, el departamento tiene usuarios asociados");}

        // Si el departamento tiene tareas no se puede borrar
        if(!delDep.getTasks().isEmpty()) { throw new NotFoundDepartmentException("Error al eliminar, el departamento tiene tareas asociadas");}


        repo.delete(delDep);
    }

    /**
     * Retrieves a list of all departments, converting them into DTO representations.
     *
     * @return a list of {@code DepartmentDto} objects representing all existing departments.
     */
    public List<DepartmentDto> findAll(){
        return
                repo.findAll().stream().map(DepartmentMapper::toDto).toList();
    }

    /**
     * Retrieves a department by its unique identifier and converts it into a DepartmentDto.
     * Throws a NoSuchElementException if the department is not found.
     *
     * @param id the unique identifier of the department to retrieve. Must not be null.
     * @return a {@code DepartmentDto} representing the retrieved department.
     * @throws NoSuchElementException if no department with the given ID exists.
     */
    public DepartmentDto findById(Long id){
        return DepartmentMapper.toDto(
                        repo.findById(id).orElseThrow(() -> new NoSuchElementException ("Departamento no encontrado"))
                );
    }


}
