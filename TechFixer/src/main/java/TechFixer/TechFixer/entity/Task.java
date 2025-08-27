package TechFixer.TechFixer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "tasks")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"dpt", "author", "ownerUser"})
public class Task {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime dateIni;
    private LocalDateTime dateEnd;
    private String description;

    @ManyToOne
    @JoinColumn(name = "dpt_id")
    private Department dpt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User ownerUser;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @OneToMany(mappedBy = "task")
    @OrderBy("date DESC")
    private List<Process> processes;

}
