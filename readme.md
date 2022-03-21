See challenge [instructions](instructions.md)

### How to use this spring-boot project
- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.

#### Restrictions
- use java 8

### Enhancements
- I added paging and sorting parameters for querying all employees
  - pageNo, pageSize, sortBy 
- I added some data to being loaded at startup. So querying the employees should return data.
- I added logs using SL4J.
  - Two log files are created error.log and debug.log they are rotated every day.
  - Configuration can be found in file [logback.xml](src/main/resources/logback.xml)
- I added the following tests:
  - I used Junit5 and Mockito for testing libraries.
  - Unit tests for Employee Service.
  - Unit tests for Employee Controller (not fully covered).
  - Integration test for the web layer in Employee Controller (not fully covered).
- I added a way to handle exceptions and return the right http error code.
- Protect controller end points:
  - configuration for Spring security can be found [CustomWebSecurityConfigurerAdapter.java](src/main/java/jp/co/axa/apidemo/configuration/CustomWebSecurityConfigurerAdapter.java) 
  - The first step I did was to use a different object(dto) to call the endpoints so I could validate its values
  - Then I used spring security to add basic authentication to call the endpoints
- Add caching logic for database calls:
  - Configuration for cache can be found [CachingConfig.java](src/main/java/jp/co/axa/apidemo/configuration/CachingConfig.java) 
  - I added the logic using the cache offered by spring
- Improved doc and comments
- Fixed bugs found
### Assumptions
- I assumed that each employee had its own unique id but all the other fields (name, salary, and department) could be non unique.
### Future work
- If I had more time I would like to create a department schema + rest controller to manipulate the departments from the company, and not having them as string in the employee.
- I would also add a jwt token for authenticate and authorize requests.
- Add an error code to the [EmployeeServiceException.java](src/main/java/jp/co/axa/apidemo/services/EmployeeServiceException.java) so the ExceptionHandler could return different status depending on it.
- More test should be added (finish coverage and load test or performance test)
### My experience with java and spring boot
I have been working as a java software engineer for the past 5 years. And with spring boot for the past 3.
