package com.credit.bladi.datasource.servicing;

import com.credit.bladi.datasource.servicing.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDatasource extends JpaRepository<PaymentEntity, Long> {
}
