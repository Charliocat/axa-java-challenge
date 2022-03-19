package jp.co.axa.apidemo.services;

import java.util.List;
import jp.co.axa.apidemo.entities.Employee;

public interface EmployeeService {

  List<Employee> retrieveEmployees();

  Employee getEmployee(Long employeeId);

  void saveEmployee(Employee employee);

  void deleteEmployee(Long employeeId);

  void updateEmployee(Employee employee);

}