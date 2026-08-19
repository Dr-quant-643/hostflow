package com.hostflow.identity.entity;

import com.hostflow.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_product", nullable = false)
    private OrganizationProduct primaryProduct;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected Organization() {
    }

    public Organization(String name, String slug, OrganizationProduct primaryProduct) {
        this.name = name;
        this.slug = slug;
        this.primaryProduct = primaryProduct;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public OrganizationProduct getPrimaryProduct() {
        return primaryProduct;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
