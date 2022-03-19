package jp.co.axa.apidemo.model;

import java.util.List;
import jp.co.axa.apidemo.entities.Employee;
import lombok.Getter;
import lombok.Setter;

public class EmployeesDTO {

  @Getter
  @Setter
  private List<Employee> employees;

}
