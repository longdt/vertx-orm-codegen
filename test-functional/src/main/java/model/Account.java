package model;

import com.github.longdt.vertxorm.annotation.NamingStrategy;
import com.github.longdt.vertxorm.format.Case;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NamingStrategy(Case.SNAKE_CASE)
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }
}
