package jp.co.axa.apidemo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EmployeeServiceImplTest {

  private static final String EMPLOYEE_1_NAME = "Akira";
  private static final int EMPLOYEE_1_SALARY = 500;
  private static final String EMPLOYEE_1_DEPARTMENT = "Sales";
  private static final String EMPLOYEE_2_NAME = "Tsunako";
  private static final int EMPLOYEE_2_SALARY = 5800;
  private static final String EMPLOYEE_2_DEPARTMENT = "Engineering";
  private static final long EMPLOYEE_ID = 1L;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeServiceImpl service;

  @Test
  void retrieveAllEmployeesInOnePage() throws Exception {
    final Pageable pageable = Mockito.mock(Pageable.class);
    final Employee employee1 = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);
    final Employee employee2 = createEmployee(EMPLOYEE_2_NAME, EMPLOYEE_2_SALARY, EMPLOYEE_2_DEPARTMENT);
    final PageImpl<Employee> pagedResult = new PageImpl(Arrays.asList(employee1, employee2), pageable, EMPLOYEE_ID);
    when(employeeRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);

    final List<Employee> employees = service.retrieveEmployees(1, 1, "name");

    assertThat(employees.size()).isEqualTo(2);
    assertThat(employees.get(0).getName()).isEqualTo(EMPLOYEE_1_NAME);
  }

  @Test
  void retrieveAllEmployeesIsEmpty() throws Exception {
    final Pageable pageable = Mockito.mock(Pageable.class);
    final PageImpl<Employee> pagedResult = new PageImpl(Arrays.asList(), pageable, EMPLOYEE_ID);
    when(employeeRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);

    final List<Employee> employees = service.retrieveEmployees(1, 1, "name");

    assertTrue(employees.isEmpty());
  }

  @Test
  void getEmployeeById() throws Exception {
    final Employee expectedEmployee = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);
    when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(expectedEmployee));

    final Employee employee = service.getEmployee(EMPLOYEE_ID);

    assertEquals(expectedEmployee, employee);
  }

  @Test
  void getEmployeeByIdThrowsEmployeeServiceException() throws Exception {
    when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.ofNullable(null));

    final EmployeeServiceException employeeServiceException = assertThrows(EmployeeServiceException.class, () -> service.getEmployee(EMPLOYEE_ID));

    assertThat(employeeServiceException.getMessage()).isEqualTo(String.format(EmployeeServiceErrorMessage.EMPLOYEE_NOT_FOUND, EMPLOYEE_ID));
  }

  @Test
  void saveEmployee() throws Exception {
    final Employee employee = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);

    service.saveEmployee(employee);

    verify(employeeRepository).save(employee);
  }

  @Test
  void updateEmployee() throws Exception {
    final Employee employee = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);

    service.updateEmployee(employee);

    verify(employeeRepository).save(employee);
  }

  @Test
  void deleteEmployeeById() throws Exception {
    service.deleteEmployee(EMPLOYEE_ID);

    verify(employeeRepository).deleteById(EMPLOYEE_ID);
  }

  @Test
  void deleteEmployeeByIdThrowsEmployeeServiceException() throws Exception {
    doThrow(new RuntimeException()).when(employeeRepository).deleteById(EMPLOYEE_ID);

    final EmployeeServiceException employeeServiceException = assertThrows(EmployeeServiceException.class, () -> service.deleteEmployee(EMPLOYEE_ID));

    assertThat(employeeServiceException.getMessage()).isEqualTo(String.format(EmployeeServiceErrorMessage.EMPLOYEE_NOT_DELETED, EMPLOYEE_ID));
    verify(employeeRepository).deleteById(EMPLOYEE_ID);
  }

  private static Employee createEmployee(String name, int salary, String department) {
    final Employee employee = new Employee();
    employee.setName(name);
    employee.setSalary(salary);
    employee.setDepartment(department);
    return employee;
  }

}
