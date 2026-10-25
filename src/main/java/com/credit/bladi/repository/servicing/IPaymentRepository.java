package com.credit.bladi.repository.servicing;

import com.credit.bladi.model.servicing.PaymentModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;

public interface IPaymentRepository {
    PaymentModel create(Long installmentId, Money amount, Instant paidAt);
}
