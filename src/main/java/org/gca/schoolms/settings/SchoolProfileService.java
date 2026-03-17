package org.gca.schoolms.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolProfileService {

    private static final Long DEFAULT_ID = 1L;

    private final SchoolProfileRepository schoolProfileRepository;

    public SchoolProfileService(SchoolProfileRepository schoolProfileRepository) {
        this.schoolProfileRepository = schoolProfileRepository;
    }

    @Transactional(readOnly = true)
    public SchoolProfileForm loadForm() {
        SchoolProfile profile = loadProfile();
        SchoolProfileForm form = new SchoolProfileForm();
        form.setSchoolName(profile.getSchoolName());
        form.setEmailAddress(profile.getEmailAddress());
        form.setPhoneNumber(profile.getPhoneNumber());
        form.setMailingAddressLine1(profile.getMailingAddressLine1());
        form.setMailingAddressLine2(profile.getMailingAddressLine2());
        form.setMailingCity(profile.getMailingCity());
        form.setMailingState(profile.getMailingState());
        form.setMailingPostalCode(profile.getMailingPostalCode());
        return form;
    }

    @Transactional(readOnly = true)
    public SchoolProfileView loadView() {
        SchoolProfile profile = loadProfile();
        return new SchoolProfileView(
            profile.getSchoolName(),
            profile.getEmailAddress(),
            profile.getPhoneNumber(),
            profile.getMailingAddressLine1(),
            profile.getMailingAddressLine2(),
            profile.getMailingCity(),
            profile.getMailingState(),
            profile.getMailingPostalCode()
        );
    }

    @Transactional
    public void update(SchoolProfileForm form) {
        SchoolProfile profile = loadProfile();
        profile.updateFrom(form);
        schoolProfileRepository.save(profile);
    }

    @Transactional
    public void ensureDefaultProfile() {
        if (schoolProfileRepository.existsById(DEFAULT_ID)) {
            return;
        }
        schoolProfileRepository.save(new SchoolProfile(
            DEFAULT_ID,
            "Grace Christian Academy",
            "info@gca.example.org",
            "670-555-1000",
            "123 Palm Street",
            "",
            "Saipan",
            "MP",
            "96950"
        ));
    }

    private SchoolProfile loadProfile() {
        return schoolProfileRepository.findById(DEFAULT_ID)
            .orElseGet(() -> schoolProfileRepository.save(new SchoolProfile(
                DEFAULT_ID,
                "Grace Christian Academy",
                "info@gca.example.org",
                "670-555-1000",
                "123 Palm Street",
                "",
                "Saipan",
                "MP",
                "96950"
            )));
    }
}
