## Vertx-Orm-Codegen
Simple annotation processor which automatic generate Repository implementation which sound like Spring does
## Developers
### Testing
Out of the box, the test suite runs a Docker container using TestContainers.
### Maven dependency
```
<dependency>
    <groupId>com.github.longdt</groupId>
    <artifactId>vertx-orm-codegen</artifactId>
    <version>1.0.1</version>
</dependency>
```
##### Add one vertx-orm-* library to use
```
<dependency>
    <groupId>com.github.longdt</groupId>
    <artifactId>vertx-orm-postgresql</artifactId>
    <version>1.2.0</version>
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
The repository implementation will be generated as follow: 
```
public class RuleTemplateRepositoryFactory extends AbstractCrudRepository<Integer, RuleTemplate> implements RuleTemplateRepository {
    public RuleTemplateRepositoryImpl(Pool pool) {
        var argumentsConverter = new ArgumentsConverter();
        var mapperBuilder = RowMapper.<Integer, RuleTemplate>builder("rule_template", RuleTemplate::new)
                .pk("id", RuleTemplate::getId, RuleTemplate::setId, true)
                .addField("name", RuleTemplate::getName, RuleTemplate::setName)
    		    .addField("arguments", RuleTemplate::getArguments, RuleTemplate::setArguments, argumentsConverter::convertToDatabaseColumn, argumentsConverter::convertToEntityAttribute)
                .addField("flink_job", RuleTemplate::getFlinkJob, RuleTemplate::setFlinkJob)
                .addField("active", RuleTemplate::getActive, RuleTemplate::setActive)
                .addField("created_at", RuleTemplate::getCreatedAt, RuleTemplate::setCreatedAt)
                .addField("updated_at", RuleTemplate::getUpdatedAt, RuleTemplate::setUpdatedAt);

        init(pool, (RowMapperImpl<Integer, RuleTemplate>) mapperBuilder.build());
    }
}
```
##### Create repository instance:
```
RuleTemplateRepository repository = new RuleTemplateRepositoryFactory(pool);
```
##### Now it's time to use. Let's try some simple methods:
###### insert
```
var template = new RuleTemplate();
...
repository.insert(template, ar -> {
    if (ar.succeeded()) {
        System.out.println(ar.result());
    } else {
        ar.cause().printStackTrace();
    }
});
```
###### update
```
var template = new RuleTemplate().setId(1);
...
repository.update(template, ar -> {
    if (ar.succeeded()) {
        System.out.println(ar.result());
    } else {
        ar.cause().printStackTrace();
    }
});
```
###### find by id
```
repository.find(id, ar -> {
    if (ar.succeeded()) {
        System.out.println(ar.result());
    } else {
        ar.cause().printStackTrace();
    }
});
```
###### find by query
```
import static com.github.longdt.vertxorm.repository.query.QueryFactory.*;

var query = QueryFactory.<RuleTemplate>and("active", 1);
repository.findAll(query, ar -> {
    if (ar.succeeded()) {
        System.out.println(ar.result());
    } else {
        ar.cause().printStackTrace();
    }
});
```
###### find with paging
```
import static com.github.longdt.vertxorm.repository.query.QueryFactory.*;

var pageRequest = new PageRequest(1, 20);
var query = QueryFactory.<RuleTemplate>and("active", 1);
repository.findAll(query, pageRequest, ar -> {
    if (ar.succeeded()) {
        System.out.println(ar.result());
    } else {
        ar.cause().printStackTrace();
    }
});
```
###### transaction with SQLHelper
```
//find then update example
var id = 1;
SQLHelper.inTransactionSingle(repository.getPool()
        , conn -> repository.find(conn, id)     //find entity by id
                .map(entityOpt -> entityOpt.orElseThrow(() -> new EntityNotFoundException("id: " + id + " is not found")))
                .compose(entity -> {
                    //update entity
                    entity.setUpdatedAt(LocalDateTime.now());
                    return repository.update(conn, entity);
                })
        , ar -> {   //handle result of transaction
            if (ar.succeeded()) {
                System.out.println(ar.result());
            } else {
                ar.cause().printStackTrace();
            }
        });
```