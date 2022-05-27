## Vertx-Orm-Codegen
Simple annotation processor which generates Repository implementation
## Developers
### Testing
Out of the box, the test suite runs a Docker container using TestContainers.
### Maven dependency
```
<dependency>
    <groupId>com.github.longdt</groupId>
    <artifactId>vertx-orm-codegen</artifactId>
    <version>2.2.0</version>
    <scope>provided</scope>
</dependency>
```
##### Add one vertx-orm-* library to use
```
<dependency>
    <groupId>com.github.longdt</groupId>
    <artifactId>vertx-orm-postgresql</artifactId>
    <version>2.3.2</version>
</dependency>
```
### Example
##### Define Entity class:

```
@Entity
public class RuleTemplate {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Convert(converter = ArguementsConverter.class)
    private Map<String, ArgumentDescription> arguments;
    private String flinkJob;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    ...
    setter/getter methods
    ...
}
```
##### Define Repository:
```
@Repository
public interface RuleTemplateRepository extends CrudRepository<Integer, RuleTemplate> {
}
```
Now it time to compile project via maven command: `mvn compile`.
The repository implementation will be generated: RuleTemplateRepositoryPostgres
##### Create repository instance:
```
RuleTemplateRepository repository = new RuleTemplateRepositoryPostgres(pool);
```
##### Now it's time to use
