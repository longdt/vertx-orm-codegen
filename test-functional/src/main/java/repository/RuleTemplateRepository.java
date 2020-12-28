package repository;


import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import com.github.longdt.vertxorm.repository.SqlDialect;
import io.vertx.core.Future;
import model.RuleTemplate;
import repository.impl.RuleTemplateRepositoryImpl;

import java.util.UUID;

@Repository(extending = RuleTemplateRepositoryImpl.class, dialect = SqlDialect.MYSQL)
public interface RuleTemplateRepository extends CrudRepository<UUID, RuleTemplate> {
    Future<Long> doSomething(int a);
}
