package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_contact_assoc_local", indexes = {
    @Index(name = "idx_student_contact_assoc_student", columnList = "student_dcid"),
    @Index(name = "idx_student_contact_assoc_person", columnList = "person_id")
})
public class PowerSchoolStudentContactAssoc {

    @Id
    @Column(name = "student_contact_assoc_id")
    private Integer studentContactAssocId;

    @Column(name = "student_dcid")
    private Integer studentDcid;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "contact_priority_order")
    private Integer contactPriorityOrder;

    @Column(name = "relationship_type_code_set_id")
    private Integer relationshipTypeCodeSetId;
}
