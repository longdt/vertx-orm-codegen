package model;

import javax.persistence.Entity;
import java.util.Map;

@Entity
public class RuleTemplate {
    private Long id;
    private String name;
    private Map<String, ArgumentDescription> arguments;
    private String flinkJob;
    private String assignee;
    private boolean active;

    public RuleTemplate() {
    }

    public Long getId() {
        return id;
    }

    public RuleTemplate setId(Long id) {
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
