package jp.co.axa.apidemo.services;

import javax.persistence.EntityNotFoundException;
import jp.co.axa.apidemo.controllers.EmployeeController;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    /**
     *
     * @param employeeId
     * @return Employee
     * @throws EntityNotFoundException
     */
    @Override
    @Cacheable("employees")
    public Employee getEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void saveEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    @Override
    @CacheEvict("employees")
    public void deleteEmployee(Long employeeId){
        employeeRepository.deleteById(employeeId);
    }

    @Override
    @CacheEvict(value = "employees", key = "#employee.id")
    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

}
