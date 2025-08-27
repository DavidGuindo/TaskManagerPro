package TechFixer.TechFixer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "departments")
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String userName;
    private String password;

    @ManyToMany
    @JoinTable(
        name = "users_dpts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "dpt_id")
    )
    private List<Department> departments;

    @OneToMany(mappedBy = "author")
    private List<Task> authorTasks;

    @OneToMany(mappedBy = "ownerUser")
    private List<Task> ownerTasks;

    @OneToMany(mappedBy = "author")
    private List<Process> processes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Roles rol;


    public User(String userName, String password, Roles rol, List<Department> departments){
        this.userName = userName;
        this.password = password;
        this.rol = rol;
        this.departments = departments;
    }

}
