package f1driversCRUD.F1Drivers.repo;

import f1driversCRUD.F1Drivers.model.Driver;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DriverRepository extends CrudRepository<Driver, Long> {
    List<Driver> findAll();
    List<Driver> findBycurrentDriverNumber(int currentDriverNumber);
    List<Driver> findByteam(String team);
    List<Driver> findBycountry(String country);
    List<Driver> findBylastName(String lastName);
}

