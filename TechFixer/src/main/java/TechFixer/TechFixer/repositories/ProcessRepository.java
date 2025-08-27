package TechFixer.TechFixer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TechFixer.TechFixer.entity.Process;


@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {


}
