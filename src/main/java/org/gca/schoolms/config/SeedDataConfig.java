package org.gca.schoolms.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.gca.schoolms.academics.Section;
import org.gca.schoolms.academics.SectionRepository;
import org.gca.schoolms.finance.Invoice;
import org.gca.schoolms.finance.InvoiceRepository;
import org.gca.schoolms.finance.InvoiceStatus;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(StudentRepository studentRepository, InvoiceRepository invoiceRepository,
                               SectionRepository sectionRepository) {
        return args -> {
            if (studentRepository.count() == 0) {
                studentRepository.save(new Student("2026-001", "Ava", "Cruz", LocalDate.of(2010, 5, 12), StudentStatus.ACTIVE));
                studentRepository.save(new Student("2026-002", "Micah", "Santos", LocalDate.of(2011, 9, 3), StudentStatus.ACTIVE));
                studentRepository.save(new Student("2025-031", "Leah", "Palomo", LocalDate.of(2008, 12, 14), StudentStatus.GRADUATED));
            }
            if (invoiceRepository.count() == 0) {
                invoiceRepository.save(new Invoice("Cruz Family", "Tuition - Semester 2", new BigDecimal("2400.00"),
                    new BigDecimal("600.00"), InvoiceStatus.PARTIAL, LocalDate.now().plusDays(10)));
                invoiceRepository.save(new Invoice("Santos Family", "Enrollment and activity fees", new BigDecimal("450.00"),
                    new BigDecimal("450.00"), InvoiceStatus.OPEN, LocalDate.now().plusDays(5)));
            }
            if (sectionRepository.count() == 0) {
                sectionRepository.save(new Section("2025-2026 Spring", "ENG-09", "English 9", "M. Perez"));
                sectionRepository.save(new Section("2025-2026 Spring", "ALG-1", "Algebra I", "D. Flores"));
                sectionRepository.save(new Section("2025-2026 Spring", "BIO-1", "Biology", "R. Tenorio"));
            }
        };
    }
}
