package TechFixer.TechFixer.repositories;

import TechFixer.TechFixer.entity.Department;
import TechFixer.TechFixer.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(String name);

}
