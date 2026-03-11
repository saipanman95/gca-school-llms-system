package org.gca.schoolms.organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String island;

    @Column(nullable = false)
    private boolean active;

    protected Campus() {
    }

    public Campus(String code, String name, String island, boolean active) {
        this.code = code;
        this.name = name;
        this.island = island;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getIsland() {
        return island;
    }

    public boolean isActive() {
        return active;
    }

    public String getDisplayName() {
        return name + " (" + island + ")";
    }
}
