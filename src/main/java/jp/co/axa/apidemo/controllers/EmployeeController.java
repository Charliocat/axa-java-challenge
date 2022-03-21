package jp.co.axa.apidemo.controllers;

import java.util.List;
import javax.validation.Valid;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.model.EmployeeDTO;
import jp.co.axa.apidemo.model.EmployeesDTO;
import jp.co.axa.apidemo.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

  private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

  @Autowired
  private EmployeeService employeeService;

  public void setEmployeeService(final EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EmployeesDTO> getEmployees(@RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "3") Integer pageSize,
                                                   @RequestParam(defaultValue = "id") String sortBy) {
    final List<Employee> employees = employeeService.retrieveEmployees(pageNo, pageSize, sortBy);
    final EmployeesDTO employeesDTO = new EmployeesDTO();
    employeesDTO.setEmployees(employees);
    return new ResponseEntity<>(employeesDTO, HttpStatus.OK);
  }

  @GetMapping(value = "/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Employee> getEmployee(@PathVariable(name = "employeeId") Long employeeId) {
    final Employee employee = employeeService.getEmployee(employeeId);
    return new ResponseEntity<>(employee, HttpStatus.OK);
  }

  @PostMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> saveEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) {
    final Employee employee = converToEmployee(employeeDTO);
    employeeService.saveEmployee(employee);
    logger.info("Employee {} Saved Successfully", employee.getId());
    return new ResponseEntity<>(String.format("{\"id\": %d }", employee.getId()), HttpStatus.CREATED);
  }

  @DeleteMapping("/employees/{employeeId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {
    employeeService.deleteEmployee(employeeId);
    logger.info("Employee {} Deleted Successfully", employeeId);
  }

  @PutMapping("/employees/{employeeId}")
  @ResponseStatus(HttpStatus.OK)
  public void updateEmployee(@RequestBody @Valid EmployeeDTO employeeDTO, @PathVariable(name = "employeeId") Long employeeId) {
    final Employee employeeToUpdate = employeeService.getEmployee(employeeId);
    if (employeeToUpdate != null) {
      employeeToUpdate.setName(employeeDTO.getName());
      employeeToUpdate.setSalary(employeeDTO.getSalary());
      employeeToUpdate.setDepartment(employeeDTO.getDepartment());
      employeeService.updateEmployee(employeeToUpdate);
      logger.info("Employee {} Updated Successfully", employeeId);
    }

  }

  private Employee converToEmployee(final EmployeeDTO employeeModel) {
    final Employee employee = new Employee();
    employee.setName(employeeModel.getName());
    employee.setSalary(employeeModel.getSalary());
    employee.setDepartment(employeeModel.getDepartment());
    return employee;
  }

}
