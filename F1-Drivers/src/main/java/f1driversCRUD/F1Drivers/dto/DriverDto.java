package f1driversCRUD.F1Drivers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String country;
    private int age;
    private String team;
    private Integer numberOfWorldChampionships;
    private Integer currentDriverNumber;
}
