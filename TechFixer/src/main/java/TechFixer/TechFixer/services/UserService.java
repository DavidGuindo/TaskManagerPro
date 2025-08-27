package TechFixer.TechFixer.services;

import TechFixer.TechFixer.dto.AuthRequest;
import TechFixer.TechFixer.dto.UserDto;
import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.Roles;
import TechFixer.TechFixer.entity.User;
import TechFixer.TechFixer.exception.NotFoundUserException;
import TechFixer.TechFixer.exception.UserAlreadyExistException;
import TechFixer.TechFixer.mapper.UserMapper;
import TechFixer.TechFixer.repositories.DepartmentRepository;
import TechFixer.TechFixer.repositories.RolesRepository;
import TechFixer.TechFixer.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Data
public class UserService {

    private final UserRepository repo;
    private final DepartmentRepository repoDpt;
    private final RolesRepository repoRol;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void iniValues(){
        if(repoRol.count() == 0 ){
            repoRol.saveAll(List.of(
                    new Roles( "ROLE_ADMIN"),
                    new Roles("ROLE_ESTD")
            ));
        }
    }



    /**
     * Saves a new user based on the provided authentication request.
     * Validates that the username and password are provided and ensures
     * the username does not already exist in the system.
     *
     * @param request the authentication request containing the user's username and password
     * @throws IllegalArgumentException if the username or password is null
     * @throws UserAlreadyExistException if a user with the specified username already exists
     */
    public void save(AuthRequest request){

        //Comprobamos que tenemos el usuario y la contraseña, sino no lo podemos dar de alta
        if(request.getUserName() == null || request.getPassword() == null){
            throw new IllegalArgumentException("Es necesario rellenar usuario y contraseña");
        }
        // Comprobamos si el usuario existe, sino excepcion
        if (repo.findByUserName(request.getUserName()).isPresent()){
            throw new UserAlreadyExistException("El usuario ya existe");
        }

        // Comporbamos si nos han pasado un rol, sino asignamos uno por defeecto
        Roles rol = new Roles();
        if(request.getRolID() != null){
            rol = repoRol.findById(request.getRolID()).orElseThrow(() -> new NoSuchElementException("Rol " + request.getRolID() + " no encontrado"));
        } else {
            rol = repoRol.findById(2L).orElseThrow(() -> new NoSuchElementException("Rol 2 no encontrado"));
        }

        User user = new User(request.getUserName(), passwordEncoder.encode(request.getPassword()), rol, List.of());

        // ¿Nos han pasado departamentos?
        if(request.getDepartmentsID() != null && !request.getDepartmentsID().isEmpty()){
            // Comprobamos que los departamentos existan
            List<Department> departments = new ArrayList<>();
            request.getDepartmentsID().forEach(id -> {
                departments.add(repoDpt.findById(id).orElseThrow(() -> new NoSuchElementException("Departamento " + id + " no encontrado")));
            });

            user.setDepartments(departments);
        }
        repo.save(user);
    }

    /**
     * Updates an existing user's details based on the provided UserDto.
     * Only non-null and non-empty fields in the UserDto will be updated.
     * If a field in the UserDto is null, the corresponding field in the user will remain unchanged.
     * If the department IDs in the UserDto are null or empty, all departments will be removed from the user.
     *
     * @param newUser the UserDto containing the updated user details. It must have a valid ID matching an existing user.
     *                The fields that can be updated are userName, password, rol, and departments_ID.
     * @throws NoSuchElementException if the user with the specified ID is not found or if any department ID in departments_ID does not exist.
     */
    public void update(UserDto newUser){
        User userToUpdate = repo.findById(newUser.getId()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // ¿Hay que actualizar el nombre? Antes de hacerlo comprobamos que no exista otro con el mismo nombre
        if(newUser.getUserName() != null && !newUser.getUserName().isEmpty()){
            // Comprobamos si existe otro usuario con el mismo nombre pero distinto ID
            if(repo.findByUserNameAndIdNot(newUser.getUserName(), newUser.getId()).isPresent()){
                throw new NoSuchElementException("El nombre indicado ya existe, escoge otro por favor");
            }
            userToUpdate.setUserName(newUser.getUserName());
       }

        // ¿Hay que actualizar la contraseña?
        if(newUser.getPassword() != null && !newUser.getPassword().isEmpty()){
            userToUpdate.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        // ¿Hay que cambiar el rol?
        if(newUser.getRolID() != null){
            userToUpdate.setRol(
                    repoRol.findById( newUser.getRolID() )
                            .orElseThrow( () -> new NoSuchElementException("Rol " + newUser.getRol() + " no encontrado")
                    )
            );

        }

        // ¿Se le han cambiado los departamentos? Se le actualizan y sino no nos pasan nada significa que hay que quitarselos
        if(newUser.getDepartmentsID() != null && !newUser.getDepartmentsID().isEmpty()){
            List<Department> departments = new ArrayList<>();
            newUser.getDepartmentsID().forEach(id -> {
                departments.add(repoDpt.findById(id).orElseThrow(() -> new NoSuchElementException("Departamento " + id + " no encontrado")));
            });
            userToUpdate.setDepartments(departments);
        } else {userToUpdate.setDepartments(null);}

        repo.save(userToUpdate);
    }

    /**
     * Deletes a user by their unique identifier. If the user does not exist, throws a NotFoundUserException.
     *
     * @param id the unique identifier of the user to be deleted
     * @throws NotFoundUserException if no user is found with the specified identifier
     */
    @Transactional
    public void deleteById(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new NotFoundUserException("Usuario " + id + " no encontrado"));

        // ¿Tiene departamentos asociados? Los quitamos
        if(!user.getDepartments().isEmpty()){
            user.setDepartments(new ArrayList<>());
            repo.save(user);
        }

        // Si ha creado tareas o tiene tareas asignadas no se puede borrar
        if(!user.getAuthorTasks().isEmpty() || !user.getOwnerTasks().isEmpty()){
            throw new NotFoundUserException("El usuario tiene tareas asignadas o creadas por el mismo, no se puede borrar");
        }

        repo.delete(user);

    }

    /**
     * Searches for a user by their username and returns user details.
     *
     * @param name the username of the user to be searched
     * @return the user details of the specified user
     * @throws UsernameNotFoundException if no user is found with the specified username
     */
    public UserDetails searchByNameDetails(String name) throws UsernameNotFoundException {
        User user = repo.findByUserName(name).orElseThrow(() -> new UsernameNotFoundException("Usuario " + name + " no encontrado"));

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRol().getName()))
        );
    }

    public UserDto searchByName(String name) throws UsernameNotFoundException {
        User user = repo.findByUserName(name).orElseThrow(() -> new UsernameNotFoundException("Usuario " + name + " no encontrado"));

        return UserMapper.toDto(user);
    }

    /**
     * Retrieves a user by their unique identifier and maps it to a UserDto object.
     *
     * @param id the unique identifier of the user to be retrieved*/
    public User findById(Long id){
        return repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Usuario " + id + " no encontrado"));
    }

    /**
     * Retrieves a user by their unique identifier, converts the User entity to a UserDto object,
     * and returns the resulting UserDto.
     *
     * @param id the unique identifier of the user to retrieve
     * @return a UserDto object representing the user with the specified identifier
     * @throws UsernameNotFoundException if no user is found with the specified identifier
     */
    public UserDto findByIdDto(Long id){
        return UserMapper.toDto(
                repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Usuario " + id + " no encontrado"))
        );
    }

    /**
     * Retrieves a list of all users and maps them to UserDto objects.
     *
     * @return a list of UserDto objects representing all users in the repository
     */
    public List<UserDto> findAllUsers(){
        return
                repo.findAll().stream().map(UserMapper::toDto).toList();
    }

    /**
     * Retrieves a list of all users and maps them to UserDto objects.
     *
     * @return a list of UserDto objects representing all users in the repository
     */
    public List<Roles> findAllRoles(){


        return
            repoRol.findAll().stream().toList();
    }


    /**
     * Removes the specified department from the user's list of associated departments.
     * If either the user or the department is not found, a {@link NoSuchElementException} is thrown.
     *
     * @param idUser the unique identifier of the user from whom the department will be removed
     * @param idDpt the unique identifier of the department to be removed
     * @throws NoSuchElementException if the user or department is not found in the repository
     */
    public void removeDepartment(Long idUser, Long idDpt){
        User user = repo.findById(idUser).orElseThrow(() -> new NoSuchElementException("Usuario no existe"));
        Department  dpt = repoDpt.findById(idDpt).orElseThrow(() -> new NoSuchElementException("Departamento no existe"));

        user.getDepartments().removeIf(d -> d.getName().equals(dpt.getName()));
        repo.save(user);

    }

    /**
     * Adds a department to a specific user's list of associated departments.
     * The user and the department must exist in the repository.
     * If either the user or the department is not found, a {@link NoSuchElementException} is thrown.
     *
     * @param idUser the unique identifier of the user to whom the department will be added
     * @param idDpt the unique identifier of the department to be added to the user
     * @throws NoSuchElementException if the user or department is not found in the repository
     */
    public void addDepartment(Long idUser, Long idDpt){
        //Obtenemos usuario y departamento
        User user = repo.findById(idUser).orElseThrow(() -> new NoSuchElementException("Usuario " + idUser + " no existe"));
        Department  dpt = repoDpt.findById(idDpt).orElseThrow(() -> new NoSuchElementException("Departamento " + idDpt + " no existe"));

        user.getDepartments().add(dpt);
        repo.save(user);

    }

}
