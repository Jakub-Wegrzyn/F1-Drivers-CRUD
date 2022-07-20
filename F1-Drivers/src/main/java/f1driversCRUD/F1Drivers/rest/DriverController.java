package f1driversCRUD.F1Drivers.rest;

import f1driversCRUD.F1Drivers.dto.DriverDto;
import f1driversCRUD.F1Drivers.model.Driver;
import f1driversCRUD.F1Drivers.repo.DriverRepository;
import org.modelmapper.config.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.*;
import java.util.stream.Collectors;

@RestController //metody będą wywoływane przy odpowiednich żądaniach HTTP
@RequestMapping("/api/driver")
public class DriverController {
    private ModelMapper modelMapper;
    private DriverRepository driverRepository;
    List<DriverDto> driverListDto = new ArrayList<>();

    public DriverController(DriverRepository driverRepository, ModelMapper modelMapper){
        this.driverRepository = driverRepository;
        this.modelMapper = modelMapper;
    }

    private DriverDto convertToDto(Driver driver){

        return modelMapper.map(driver, DriverDto.class);
    }

    private List<DriverDto> convertToDto(List<Driver> driver){
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        return driver
                .stream()
                .map(eachdriver -> modelMapper.map(eachdriver, DriverDto.class))
                .collect(Collectors.toList());


    }

    private Driver convertToEntity(DriverDto driverdto){

        return modelMapper.map(driverdto, Driver.class);
    }

    //GET ALL DRIVERS
    @GetMapping("/getalldrivers")
    public ResponseEntity<Collection<DriverDto>> getAllDrivers(){
        List<Driver> allDrivers = driverRepository.findAll();
        List<DriverDto> result = allDrivers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //GET DRIVER/DRIVERS BY CURRENT DRIVER NUMBER
    @GetMapping("/drivernumber/")
    public ResponseEntity<Object>
    getDriverById(@RequestParam (value = "value") List<Integer> number) {
        List<Driver> driver =  null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i<number.size(); i++){
            driver = driverRepository.findBycurrentDriverNumber(number.get(i));
            driverArray.addAll(driver);
        }

        if(!driverArray.isEmpty()) {
            List<DriverDto> driversdto = convertToDto(driverArray);
            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //GET DRIVER/DRIVERS BY TEAM
    @GetMapping("/team/")
    public ResponseEntity<Object> getDriverByTeam(@RequestParam(value = "value") List<String> team) {
        List<Driver> driver =  null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i<team.size(); i++){
            driver = driverRepository.findByteam(team.get(i));
            driverArray.addAll(driver);
        }
        if(!driverArray.isEmpty()){
            List<DriverDto> driversdto = convertToDto(driverArray);

            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null,
                    HttpStatus.NOT_FOUND);
        }
    }

    //GET DRIVER/DRIVERS BY COUNTRY
    @GetMapping("/country/")
    public ResponseEntity<Object> getDriverBycountry(@RequestParam(value="value") List<String> country) {
        List<Driver> driver = null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i =0; i<country.size(); i++){
            driver =  driverRepository.findBycountry(country.get(i));
            driverArray.addAll(driver);
        }
        if(!driverArray.isEmpty()){
            List<DriverDto> driversdto = convertToDto(driverArray);
            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //POST ONE DRIVER (DOESN'T MATTER IF DRIVER ALREADY EXISTS - WILL BE ADDED WITH DIFFERENT ID)
    @PostMapping("/postonedriver")
    public ResponseEntity saveNewDriver(@RequestBody DriverDto imputdriver){
        driverListDto.removeAll(driverListDto);
        Driver entity = convertToEntity(imputdriver);
        driverRepository.save(entity);

        return new ResponseEntity(null, HttpStatus.CREATED);
    }

    //POST DRIVERS ARRAY (DOESN'T MATTER IF DRIVER ALREADY EXISTS - WILL BE ADDED WITH DIFFERENT ID)
    @PostMapping("/postalldrivers")
    public ResponseEntity saveNewDrivers(@RequestBody DriverDto[] imputdrivers){

        driverListDto.removeAll(driverListDto);
        for(int i = 0 ; i<imputdrivers.length; i++){
            driverListDto.add(imputdrivers[i]);
        }
        Driver entity = null;
        HttpHeaders headers = new HttpHeaders();

        for(int i = 0 ; i<driverListDto.size();i++){
            entity = convertToEntity(driverListDto.get(i));
            driverRepository.save(entity);
        }
        return new ResponseEntity(headers ,HttpStatus.CREATED);

    }

    //!!!!! IMPORTANT !!!!!!!!!!!!!  PUT ONE DRIVER (IF EXIST WITH IMPLEMENTED ID, WILL BE UPDATED. IF NOT WILL BE ADDED WITH THE NEW (AUTO GENERATED) ID
    @PutMapping("/updatedriver/{id}")
    public ResponseEntity updateOrInsertDriver(@PathVariable Long id, @RequestBody DriverDto driverDto){
        Optional<Driver> currentDriver = driverRepository.findById(id);
        if(currentDriver.isPresent()){
            driverDto.setId(id);
            Driver entity = convertToEntity(driverDto);
            driverRepository.save(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }else{
            driverDto.setId(id);
            Driver entity = convertToEntity(driverDto);
            driverRepository.save(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    //PUT DRIVER ARRAY (IF SINGLE RECORD EXIST WILL BE UPDATED. IF NOT WILL BE ADDED WITH THE NEW (AUTO GENERATED) ID
    @PutMapping("/putalldrivers")
    public ResponseEntity updateOrInsertDrivers(@RequestBody DriverDto[] imputdrivers){
        driverListDto.removeAll(driverListDto);
        List<Driver> currentDriver = null;
        ArrayList<Driver> existdriver = new ArrayList<>();
        Driver entity;
        for(int i = 0 ; i<imputdrivers.length; i++){
            driverListDto.add(imputdrivers[i]);
        }

        for(int i = 0 ; i<driverListDto.size(); i++){
            currentDriver = driverRepository.findBylastName(driverListDto.get(i).getLastName());
            existdriver.addAll(currentDriver);
            if(currentDriver.isEmpty()){
                entity = convertToEntity(driverListDto.get(i));
                driverRepository.save(entity);
                currentDriver.removeAll(currentDriver);
            }
            else{
                continue;
            }
        }
        if(existdriver.size()>0){
            for (int i = 0; i<existdriver.size(); i++){
                driverRepository.save(existdriver.get(i));
                currentDriver.removeAll(currentDriver);
            }
        }else {
            return new ResponseEntity(HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    //DELETE DRIVER/DRIVERS BY LAST NAME
    @DeleteMapping("/delete/")
    public ResponseEntity deleteDriver(@RequestParam(value = "value") List<String> lastName){
        List<Driver> driver = null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i< lastName.size(); i++){
            driver =  driverRepository.findBylastName(lastName.get(i));
            driverArray.addAll(driver);
        }
        for(int i = 0; i<driverArray.size(); i++){
            driverRepository.deleteById(driverArray.get(i).getId());
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //DELETE ALL DRIVERS
    @DeleteMapping("deleteall")
    public ResponseEntity deleteAllDrivers(){
        driverRepository.deleteAll();
        return new ResponseEntity<>( HttpStatus.OK);
    }
}

