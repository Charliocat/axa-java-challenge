package jp.co.axa.apidemo.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class EmployeeDTO {

  @Getter
  @Setter
  @NotBlank(message = "Employee name must not be empty")
  @NotNull(message = "Employee name must be provided")
  private String name;

  @Getter
  @Setter
  @NotNull(message = "Employee salary must be provided")
  private Integer salary;

  @Getter
  @Setter
  @NotBlank(message = "Employee department must not be empty")
  @NotNull(message = "Employee department must be provided")
  private String department;

}
