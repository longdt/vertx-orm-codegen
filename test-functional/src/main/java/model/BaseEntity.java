package model;

import java.time.LocalDateTime;

public abstract class BaseEntity<T> {
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public T setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return self();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public T setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return self();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public T setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return self();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public T setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return self();
    }

    protected abstract T self();

    @Override
    public String toString() {
        return "BaseEntity{" +
                "createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
