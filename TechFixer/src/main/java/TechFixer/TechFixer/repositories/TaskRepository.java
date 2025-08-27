package TechFixer.TechFixer.repositories;

import TechFixer.TechFixer.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    // Busca las tareas de un usuario segun una lista de estados
    List<Task> findByOwnerUser_IdAndState_IdInOrderByDateIniDesc(Long idUser, List<Long> estados);

}
