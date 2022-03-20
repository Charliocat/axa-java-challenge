See challenge [instructions](instructions.md)

### Enhancements
- I added logs using SL4J.
  - Two log files are created error.log and debug.log they are rotated every day.
  - Configuration can be found in file [logback.xml](src/main/resources/logback.xml)
- **TODO** Add tests
- Check syntax
- Protect controller end points:
  - configuration for Spring security can be found [CustomWebSecurityConfigurerAdapter.java](src/main/java/jp/co/axa/apidemo/configuration/CustomWebSecurityConfigurerAdapter.java) 
  - The first step I did was to use a different object(dto) to call the endpoints so I could validate its values
  - Then I used spring security to add basic authentication to call the endpoints
- Add caching logic for database calls:
  - Configuration for cache can be found [CachingConfig.java](src/main/java/jp/co/axa/apidemo/configuration/CachingConfig.java) 
  - I added the logic using the cache offered by spring
- Improve doc and comments
- Fixed bugs on any bug you might find
### Assumptions
- I assumed that each employee had its own unique id but all the other fields (name, salary, and department) could be non unique.
### Future work
- If I had more time I would like to create a department schema + rest controller to manipulate the departments from the company, and not having them as string in the employee.
- I would also add a jwt token for authenticate and authorize requests.
### My experience with java and spring boot
I have been working as a java software engineer for the past 5 years. And with spring boot for the past 3.
