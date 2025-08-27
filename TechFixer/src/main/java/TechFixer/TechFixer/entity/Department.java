package TechFixer.TechFixer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "departments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
public class Department {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "departments")
    @JsonIgnoreProperties("departments")
    private List<User> users;

    @OneToMany(mappedBy = "dpt")
    private List<Task> tasks;


    public Department(String name){
        this.name = name;
    }

    public Department(Long id, String name){ this.id = id; this.name = name;}

}
