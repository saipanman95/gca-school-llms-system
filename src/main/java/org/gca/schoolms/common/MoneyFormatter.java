package org.gca.schoolms.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component("moneyFormatter")
public class MoneyFormatter {

    public String format(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}
