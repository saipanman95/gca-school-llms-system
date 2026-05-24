package org.gca.schoolms.certificates;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public final class CertificateLabelUtil {

    private CertificateLabelUtil() {
    }

    public static String termDisplay(String term) {
        if (term == null || term.isBlank()) {
            return "";
        }
        return switch (term) {
            case "Q1" -> "1st quarter";
            case "Q2" -> "2nd quarter";
            case "Q3" -> "3rd quarter";
            case "Q4" -> "4th quarter";
            case "S1" -> "1st semester";
            case "S2" -> "2nd semester";
            default -> term;
        };
    }

    public static String schoolYearLabel(String term, int year) {
        int startYear = startsInSelectedYear(term) ? year : year - 1;
        return startYear + "-" + (startYear + 1);
    }

    public static String dayOfMonthOrdinal(LocalDate date) {
        int day = date.getDayOfMonth();
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        return switch (day % 10) {
            case 1 -> day + "st";
            case 2 -> day + "nd";
            case 3 -> day + "rd";
            default -> day + "th";
        };
    }

    public static String monthYearLabel(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + date.getYear();
    }

    public static String displayGrade(Integer gradeLevel) {
        if (gradeLevel == null) {
            return "";
        }
        if (gradeLevel == 0) {
            return "K5";
        }
        if (gradeLevel == -1) {
            return "K4";
        }
        return String.valueOf(gradeLevel);
    }

    public static int determineStudentFontSize(String studentName) {
        int length = studentName.length();
        if (length <= 20) {
            return 68;
        }
        if (length <= 30) {
            return 58;
        }
        if (length <= 40) {
            return 50;
        }
        return 44;
    }

    private static boolean startsInSelectedYear(String term) {
        return "Q1".equals(term) || "Q2".equals(term) || "S1".equals(term);
    }
}
