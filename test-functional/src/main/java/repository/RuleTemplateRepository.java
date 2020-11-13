package repository;


import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import model.RuleTemplate;
import repository.impl.RuleTemplateRepositoryImpl;

@Repository(extending = RuleTemplateRepositoryImpl.class)
public interface RuleTemplateRepository extends CrudRepository<Long, RuleTemplate> {
    void doSomething(Handler<AsyncResult<Long>> resultHandler);

    static RuleTemplateRepository create(Pool pool) {
        return new RuleTemplateRepositoryFactory(pool);
    }
}
