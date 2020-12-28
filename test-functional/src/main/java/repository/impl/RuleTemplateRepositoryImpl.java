package repository.impl;

import com.github.longdt.vertxorm.repository.mysql.AbstractCrudRepository;
import io.vertx.core.Future;
import model.RuleTemplate;
import repository.RuleTemplateRepository;

import java.util.UUID;

public class RuleTemplateRepositoryImpl extends AbstractCrudRepository<UUID, RuleTemplate> implements RuleTemplateRepository {
    @Override
    public Future<Long> doSomething(int a) {
        return Future.succeededFuture();
    }
}
