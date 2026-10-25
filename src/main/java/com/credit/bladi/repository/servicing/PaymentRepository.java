package com.credit.bladi.repository.servicing;

import com.credit.bladi.datasource.servicing.PaymentDatasource;
import com.credit.bladi.datasource.servicing.entity.PaymentEntity;
import com.credit.bladi.datasource.servicing.mapper.PaymentEntityMapper;
import com.credit.bladi.model.servicing.PaymentModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PaymentRepository implements IPaymentRepository {
    private final PaymentDatasource datasource;

    public PaymentRepository(PaymentDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public PaymentModel create(Long installmentId, Money amount, Instant paidAt) {
        PaymentEntity entity = PaymentEntityMapper.mapToNewEntity(installmentId, amount, paidAt);
        datasource.save(entity);
        return PaymentEntityMapper.mapFromEntity(entity);
    }
}
