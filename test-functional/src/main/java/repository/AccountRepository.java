package repository;

import com.github.longdt.vertxorm.annotation.Repository;
import com.github.longdt.vertxorm.repository.CrudRepository;
import com.github.longdt.vertxorm.repository.SqlDialect;
import model.Account;

@Repository(dialect = SqlDialect.MYSQL)
public interface AccountRepository extends CrudRepository<Long, Account> {
}
