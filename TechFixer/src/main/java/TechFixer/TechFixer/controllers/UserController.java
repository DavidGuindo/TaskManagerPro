package TechFixer.TechFixer.controllers;

import TechFixer.TechFixer.dto.UserDto;
import TechFixer.TechFixer.exception.NotFoundUserException;
import TechFixer.TechFixer.exception.UserAlreadyExistException;
import TechFixer.TechFixer.dto.AuthRequest;
import TechFixer.TechFixer.dto.AuthResponse;
import TechFixer.TechFixer.security.JwtUtil;
import TechFixer.TechFixer.services.UserService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
@Data
public class UserController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Handles the user registration process. Accepts an authentication request containing
     * username and password, and registers a new user. If the username already exists,
     * or an error occurs during registration, appropriate error messages are returned.
     *
     * @param request an AuthRequest object containing the username and password of the user to be registered
     * @return a ResponseEntity containing:
     *         - A success message if the user is registered successfully.
     *         - A conflict status and an error message if the user already exists.
     *         - A conflict status and a general error message for other registration failures.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request){
        try {
            userService.save(request);
            return ResponseEntity.ok("Usuario registrado");

        } catch (UserAlreadyExistException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error en el registro");
        }
    }

    /**
     * Handles user authentication by validating the provided credentials and generating a JWT token
     * upon successful authentication. If the credentials are invalid, an unauthorized response is returned.
     * In case of any other issues during the login process, a conflict status is returned.
     *
     * @param request the AuthRequest object containing the user's credentials. This includes:
     *                - userName: the username of the user attempting to log in.
     *                - password: the password of the user attempting to log in.
     * @return a ResponseEntity containing:
     *         - An AuthResponse object with the generated JWT token if authentication is successful.
     *         - An unauthorized status and error message if the credentials are invalid.
     *         - A conflict status and error message in case of other issues during the login process.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUserName(), request.getPassword()
            ));

            // Generamos el token
            final UserDetails userDetails = userService.searchByNameDetails(request.getUserName());
            final String jwt = jwtUtil.generateToken(userDetails);
            final UserDto user = userService.searchByName( request.getUserName() );

            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getRolID() ));

        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error en inicio de sesión");
        }
    }

    /**
     * Updates the details of an existing user. Accepts new user details in the request body
     * and updates the corresponding user in the database.
     *
     * @param newUser the User object containing the updated details of the user.
     *                This includes any of the following fields:
     *                - id: the unique identifier of the user to be updated (required).
     *                - userName: the new username for the user (optional).
     *                - password: the new password for the user (optional).
     *                - rol: the new role for the user (optional).
     *                - departments: the new list of departments associated with the user (optional).
     * @return a ResponseEntity indicating the result of the update operation:
     *         - A success message with HTTP status 200 if the user is updated successfully.
     *         - A NOT_FOUND status with an error message if the user is not found.
     *         - A general error message with a NOT_FOUND status in case of other issues during the update process.
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserDto newUser){
        try {
            userService.update(newUser);
            return ResponseEntity.ok("Usuario actualizado correctamente");
        } catch (NotFoundUserException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar usuario");
        }
    }

    /**
     * Deletes a user by their unique identifier. If the user is found, the user is removed successfully.
     * If the user does not exist, a {@code NotFoundUserException} is caught and an appropriate error message is returned.
     * Any other exceptions will return a general error message.
     *
     * @param userId the unique identifier of the user to be deleted
     * @return a ResponseEntity indicating the status of the delete operation:
     *         - An HTTP 200 (OK) status with a success message if the user is deleted successfully.
     *         - An HTTP 404 (NOT_FOUND) status with an error message if the user is not found.
     *         - An HTTP 404 (NOT_FOUND) status with a general error message for unexpected issues.
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        try{
            System.out.println("Vamos a borrar");
            userService.deleteById(userId);
            return ResponseEntity.ok("Usuario eliminado correctamente");

        } catch (NotFoundUserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al borrar usuario " + e.getCause() + " --- " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their unique identifier. If the user is found, the user's details are returned.
     * If the user does not exist, a {@code UsernameNotFoundException} is caught and an appropriate error message is returned.
     * Any other exceptions will return a general error message.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a ResponseEntity containing:
     *         - The user details and an HTTP 200 (OK) status if the user is found.
     *         - An HTTP 404 (NOT_FOUND) status with an error message if the user is not found.
     *         - An HTTP 404 (NOT_FOUND) status with a general error message for unexpected issues.
     */
    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId){
        try {
            return ResponseEntity.ok(userService.findByIdDto(userId));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener usuario");

        }
    }

    /**
     * Retrieves a list of all users available in the system.
     * If an error occurs during the retrieval process, an error message is returned.
     *
     * @return a ResponseEntity containing:
     *         - A list of all users with an HTTP 200 (OK) status if the retrieval is successful.
     *         - An HTTP 404 (NOT_FOUND) status with an error message in case of any issues during the retrieval process.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> findAllUsers(){
        try {
            return ResponseEntity.ok(userService.findAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener usuarios.");

        }
    }

    @GetMapping("/getAllRoles")
    public ResponseEntity<?> findAllRoles(){
        try {
            return ResponseEntity.ok(userService.findAllRoles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener roles.");

        }
    }




}
