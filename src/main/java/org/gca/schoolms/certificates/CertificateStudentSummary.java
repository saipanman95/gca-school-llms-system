package org.gca.schoolms.certificates;

public record CertificateStudentSummary(
    Long id,
    String lname,
    String fname,
    String mname,
    int gradeLevel,
    int enrollStatus,
    boolean excludeFromGpa
) {
    public String displayName() {
        StringBuilder builder = new StringBuilder();
        if (fname != null && !fname.isBlank()) {
            builder.append(fname.trim());
        }
        if (mname != null && !mname.isBlank()) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(mname.trim());
        }
        if (lname != null && !lname.isBlank()) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(lname.trim());
        }
        return builder.toString();
    }
}
