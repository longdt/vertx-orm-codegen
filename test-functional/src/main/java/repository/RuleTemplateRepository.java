package repository;


import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import model.RuleTemplate;
import repository.impl.RuleTemplateRepositoryImpl;

@Repository(extending = RuleTemplateRepositoryImpl.class)
public interface RuleTemplateRepository extends CrudRepository<Long, RuleTemplate> {
    void doOtherThing();
}
