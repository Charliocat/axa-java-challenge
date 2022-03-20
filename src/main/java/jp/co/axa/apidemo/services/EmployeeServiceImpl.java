package jp.co.axa.apidemo.services;

import java.util.List;
import java.util.Optional;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

  @Autowired
  private EmployeeRepository employeeRepository;

  public void setEmployeeRepository(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public List<Employee> retrieveEmployees() {
    return employeeRepository.findAll();
  }

  /**
   * @param employeeId
   * @return Employee
   * @throws EmployeeServiceException
   */
  @Override
  @Cacheable("employees")
  public Employee getEmployee(Long employeeId) {
    final Optional<Employee> employee = employeeRepository.findById(employeeId);
    return employee.orElseThrow(() -> new EmployeeServiceException(String.format(EmployeeServiceErrorMessage.EMPLOYEE_NOT_FOUND, employeeId)));
  }

  @Override
  public void saveEmployee(Employee employee) {
    employeeRepository.save(employee);
  }

  /**
   * @param employeeId
   * @throws EmployeeServiceException
   */
  @Override
  @CacheEvict("employees")
  public void deleteEmployee(Long employeeId) {
    try {
      employeeRepository.deleteById(employeeId);
    } catch (Exception e) {
      logger.error("Error deleting employee", e);
      throw new EmployeeServiceException(String.format(EmployeeServiceErrorMessage.EMPLOYEE_NOT_DELETED, employeeId), e);
    }
  }

  @Override
  @CacheEvict(value = "employees", key = "#employee.id")
  public void updateEmployee(Employee employee) {
    employeeRepository.save(employee);
  }

}
