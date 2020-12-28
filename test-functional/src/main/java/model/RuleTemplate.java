package model;

import com.github.longdt.vertxorm.annotation.NamingStrategy;
import com.github.longdt.vertxorm.format.Case;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;
import java.util.UUID;

@Entity
@NamingStrategy(Case.SNAKE_CASE)
public class RuleTemplate extends BaseEntity<RuleTemplate> {
    @Id
    @Convert(converter = IdConverter.class)
    private UUID id;
    private String name;
    @Convert(converter = ArgumentsConverter.class)
    private Map<String, ArgumentDescription> arguments;
    private String flinkJob;
    private String assignee;
    private boolean active;
    private RuleTemplateStatus status;

    public RuleTemplate() {
    }

    public UUID getId() {
        return id;
    }

    public RuleTemplate setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RuleTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, ArgumentDescription> getArguments() {
        return arguments;
    }

    public RuleTemplate setArguments(Map<String, ArgumentDescription> arguments) {
        this.arguments = arguments;
        return this;
    }

    public String getFlinkJob() {
        return flinkJob;
    }

    public RuleTemplate setFlinkJob(String flinkJob) {
        this.flinkJob = flinkJob;
        return this;
    }

    public String getAssignee() {
        return assignee;
    }

    public RuleTemplate setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public boolean getActive() {
        return active;
    }

    public RuleTemplate setActive(boolean active) {
        this.active = active;
        return this;
    }

    public RuleTemplateStatus getStatus() {
        return status;
    }

    public RuleTemplate setStatus(RuleTemplateStatus status) {
        this.status = status;
        return this;
    }

    @Override
    protected RuleTemplate self() {
        return this;
    }

    @Override
    public String toString() {
        return "model.RuleTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", arguments=" + arguments +
                ", flinkJob='" + flinkJob + '\'' +
                ", assignee='" + assignee + '\'' +
                ", active=" + active +
                '}';
    }
}
