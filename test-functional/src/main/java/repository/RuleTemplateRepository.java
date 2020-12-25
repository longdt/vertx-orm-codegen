package repository;


import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import com.github.longdt.vertxorm.repository.SqlDialect;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import model.RuleTemplate;
import repository.impl.RuleTemplateRepositoryImpl;

import java.util.UUID;

@Repository(extending = RuleTemplateRepositoryImpl.class, dialect = SqlDialect.MYSQL)
public interface RuleTemplateRepository extends CrudRepository<UUID, RuleTemplate> {
    void doSomething(Handler<AsyncResult<Long>> resultHandler);

    static RuleTemplateRepository create(Pool pool) {
        return new RuleTemplateRepositoryFactory(pool);
    }
}
