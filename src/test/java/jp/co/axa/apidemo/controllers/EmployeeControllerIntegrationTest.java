package jp.co.axa.apidemo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Collections;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
@AutoConfigureTestDatabase
class EmployeeControllerIntegrationTest {

  private static final long EMPLOYEE_ID = 1;
  private static final String API_V1_EMPLOYEES = "/api/v1/employees/";
  private static final String GET_EMPLOYEE_RESPONSE = "{\"id\":null,\"name\":\"Akira\",\"salary\":500,\"department\":\"Sales\"}";
  private static final String EMPLOYEE_1_NAME = "Akira";
  private static final String EMPLOYEE_2_NAME = "Mika";
  private static final int EMPLOYEE_1_SALARY = 500;
  private static final String EMPLOYEE_1_DEPARTMENT = "Sales";
  private static final String GET_EMPLOYEES_RESPONSE = "{\"employees\":[{\"id\":null,\"name\":\"Akira\",\"salary\":500,\"department\":\"Sales\"}]}";
  private static final String POST_NEW_EMPLOYEE_REQUEST = "{"
      + "\"name\":\"" + EMPLOYEE_1_NAME + "\","
      + "\"salary\":" +  EMPLOYEE_1_SALARY + ","
      + "\"department\":\"" + EMPLOYEE_1_DEPARTMENT + "\""
      + "}";
  private static final String POST_NEW_EMPLOYEE_RESPONSE = "{\"id\": null }";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void getEmployeeButNotFound() throws Exception {
    final RequestBuilder getRequest = get(API_V1_EMPLOYEES + EMPLOYEE_ID);
    final ResultActions result = mockMvc.perform(getRequest);
    result.andExpect(MockMvcResultMatchers.status().isOk());
    verify(employeeService).getEmployee(EMPLOYEE_ID);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getEmployee() throws Exception {
    final Employee employee = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);
    when(employeeService.getEmployee(EMPLOYEE_ID)).thenReturn(employee);

    final RequestBuilder getRequest = get(API_V1_EMPLOYEES + EMPLOYEE_ID);
    final ResultActions result = mockMvc.perform(getRequest);
    result.andExpect(MockMvcResultMatchers.status().isOk());

    result.andExpect(content().string(GET_EMPLOYEE_RESPONSE));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getEmployees() throws Exception {
    final Employee employee = createEmployee(EMPLOYEE_1_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);
    when(employeeService.retrieveEmployees(anyInt(), anyInt(), anyString())).thenReturn(Collections.singletonList(employee));

    final RequestBuilder getRequest = get(API_V1_EMPLOYEES);
    final ResultActions result = mockMvc.perform(getRequest);
    result.andExpect(MockMvcResultMatchers.status().isOk());

    result.andExpect(content().string(GET_EMPLOYEES_RESPONSE));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void postNewEmployee() throws Exception {
    final ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
    final RequestBuilder postRequest = post(API_V1_EMPLOYEES)
        .content(POST_NEW_EMPLOYEE_REQUEST)
        .contentType(MediaType.APPLICATION_JSON);

    final ResultActions result = mockMvc.perform(postRequest);
    result.andExpect(MockMvcResultMatchers.status().isCreated());
    result.andExpect(content().string(POST_NEW_EMPLOYEE_RESPONSE));
    verify(employeeService).saveEmployee(employeeArgumentCaptor.capture());

    assertThat(employeeArgumentCaptor.getValue().getName()).isEqualTo(EMPLOYEE_1_NAME);
    assertThat(employeeArgumentCaptor.getValue().getDepartment()).isEqualTo(EMPLOYEE_1_DEPARTMENT);
    assertThat(employeeArgumentCaptor.getValue().getSalary()).isEqualTo(EMPLOYEE_1_SALARY);
  }

  @Test
  void postNewEmployeeWithoutBeingAuthorized() throws Exception {
    final RequestBuilder postRequest = post(API_V1_EMPLOYEES)
        .content(POST_NEW_EMPLOYEE_REQUEST)
        .contentType(MediaType.APPLICATION_JSON);
    final ResultActions result = mockMvc.perform(postRequest);
    result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateExistingEmployee() throws Exception {
    final Employee existingEmployee = createEmployee(EMPLOYEE_2_NAME, EMPLOYEE_1_SALARY, EMPLOYEE_1_DEPARTMENT);
    when(employeeService.getEmployee(EMPLOYEE_ID)).thenReturn(existingEmployee);
    final ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
    final RequestBuilder putRequest = put(API_V1_EMPLOYEES + EMPLOYEE_ID)
        .content(POST_NEW_EMPLOYEE_REQUEST)
        .contentType(MediaType.APPLICATION_JSON);

    final ResultActions result = mockMvc.perform(putRequest);
    result.andExpect(MockMvcResultMatchers.status().isOk());
    verify(employeeService).updateEmployee(employeeArgumentCaptor.capture());

    assertThat(employeeArgumentCaptor.getValue().getName()).isEqualTo(EMPLOYEE_1_NAME);
    assertThat(employeeArgumentCaptor.getValue().getDepartment()).isEqualTo(EMPLOYEE_1_DEPARTMENT);
    assertThat(employeeArgumentCaptor.getValue().getSalary()).isEqualTo(EMPLOYEE_1_SALARY);
  }

  private static Employee createEmployee(String name, int salary, String department) {
    final Employee employee = new Employee();
    employee.setName(name);
    employee.setSalary(salary);
    employee.setDepartment(department);
    return employee;
  }

}
