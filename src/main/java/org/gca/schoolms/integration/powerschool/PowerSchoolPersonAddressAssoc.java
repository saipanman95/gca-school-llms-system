package org.gca.schoolms.integration.powerschool;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "person_address_assoc_local", indexes = {
    @Index(name = "idx_person_address_assoc_person", columnList = "person_id"),
    @Index(name = "idx_person_address_assoc_address", columnList = "person_address_id")
})
public class PowerSchoolPersonAddressAssoc {

    @Id
    @Column(name = "person_address_assoc_id")
    private Integer personAddressAssocId;

    @Column(name = "person_address_id")
    private Integer personAddressId;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "address_priority_order")
    private Integer addressPriorityOrder;

    @Column(name = "address_type_code_set_id")
    private Integer addressTypeCodeSetId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
