package repository.impl;

import com.github.longdt.vertxorm.repository.postgresql.AbstractCrudRepository;
import model.RuleTemplate;
import repository.RuleTemplateRepository;

public class RuleTemplateRepositoryImpl extends AbstractCrudRepository<Long, RuleTemplate> implements RuleTemplateRepository {

    @Override
    public void doOtherThing() {

    }
}
