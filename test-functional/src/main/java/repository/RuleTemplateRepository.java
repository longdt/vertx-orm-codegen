package repository;


import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import model.RuleTemplate;

@Repository
public interface RuleTemplateRepository extends CrudRepository<Long, RuleTemplate> {
}
