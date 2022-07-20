package f1driversCRUD.F1Drivers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String country;
    private String team;
    private Integer numberOfWorldChampionships;
    private Integer currentDriverNumber = null;

    @Transient
    private Integer age;
    //CALCULATE AGE FROM DATE OF BIRTH
    public Integer getAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
    public void setAge(Integer age){

        this.age = age;
    }


}