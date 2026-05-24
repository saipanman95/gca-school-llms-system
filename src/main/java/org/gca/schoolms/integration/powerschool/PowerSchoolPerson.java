package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person_local")
public class PowerSchoolPerson {

    @Id
    @Column(name = "person_id")
    private Integer personId;

    @Column(unique = true)
    private Integer dcid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_active")
    private Boolean active;
}
