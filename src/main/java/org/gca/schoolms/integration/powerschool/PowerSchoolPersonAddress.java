package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person_address_local")
public class PowerSchoolPersonAddress {

    @Id
    @Column(name = "person_address_id")
    private Integer personAddressId;

    private String street;

    @Column(name = "line_two")
    private String lineTwo;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    private String unit;

    @Column(name = "state_code_set_id")
    private Integer stateCodeSetId;
}
