package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import java.time.LocalDate;

public record ScheduledInstallment(LocalDate dueDate, Money amount) {
}
