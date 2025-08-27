package TechFixer.TechFixer.repositories;

import TechFixer.TechFixer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca un usuario por su nomobre
    Optional<User> findByUserName(String name);
    Optional<User> findByUserNameAndIdNot(String name, Long id);


}
