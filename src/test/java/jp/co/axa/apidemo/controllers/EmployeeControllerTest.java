package jp.co.axa.apidemo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.model.EmployeeDTO;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EmployeeControllerTest {

  private static final String EMPLOYEE_1_NAME = "Akira";
  private static final String EMPLOYEE_2_NAME = "Mark";
  private static final int EMPLOYEE_1_SALARY = 500;
  private static final String EMPLOYEE_1_DEPARTMENT = "Sales";

  @Mock
  private EmployeeService employeeService;

  @InjectMocks
  private EmployeeController controller;

  @Captor
  private ArgumentCaptor<Employee> employeeCaptor;

  @Test
  void createNewEmployee() throws Exception {
    final EmployeeDTO employeeDTO = EmployeeDTO.builder()
        .name(EMPLOYEE_1_NAME)
        .salary(EMPLOYEE_1_SALARY)
        .department(EMPLOYEE_1_DEPARTMENT)
        .build();

    controller.saveEmployee(employeeDTO);

    verify(employeeService).saveEmployee(employeeCaptor.capture());

    assertThat(employeeCaptor.getValue().getName()).isEqualTo(EMPLOYEE_1_NAME);
    assertThat(employeeCaptor.getValue().getDepartment()).isEqualTo(EMPLOYEE_1_DEPARTMENT);
    assertThat(employeeCaptor.getValue().getSalary()).isEqualTo(EMPLOYEE_1_SALARY);
  }

  @Test
  void updateExistingEmployee() throws Exception {
    when(employeeService.getEmployee(1L)).thenReturn(createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT));
    final EmployeeDTO employeeDTO = EmployeeDTO.builder()
        .name(EMPLOYEE_2_NAME)
        .salary(EMPLOYEE_1_SALARY)
        .department(EMPLOYEE_1_DEPARTMENT)
        .build();
    controller.updateEmployee(employeeDTO, 1L);

    verify(employeeService).updateEmployee(employeeCaptor.capture());

    assertThat(employeeCaptor.getValue().getName()).isEqualTo(EMPLOYEE_2_NAME);
    assertThat(employeeCaptor.getValue().getDepartment()).isEqualTo(EMPLOYEE_1_DEPARTMENT);
    assertThat(employeeCaptor.getValue().getSalary()).isEqualTo(EMPLOYEE_1_SALARY);
  }

  @Test
  void doNothingWhenUpdatingNonExistingEmployee() throws Exception {
    when(employeeService.getEmployee(1L)).thenReturn(null);
    final EmployeeDTO employeeDTO = EmployeeDTO.builder()
        .name(EMPLOYEE_2_NAME)
        .salary(EMPLOYEE_1_SALARY)
        .department(EMPLOYEE_1_DEPARTMENT)
        .build();
    controller.updateEmployee(employeeDTO, 1L);

    verify(employeeService, never()).updateEmployee(employeeCaptor.capture());
  }

  private static Employee createEmployee(String name, int salary, String department) {
    final Employee employee = new Employee();
    employee.setName(name);
    employee.setSalary(salary);
    employee.setDepartment(department);
    return employee;
  }

}
