package com.credit.bladi.service.servicing;

import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import java.util.List;
import java.util.UUID;

public interface ICreditFileService {
    CreditFileModel openCreditFile(Long demandId, String triggeredByUsername);

    CreditFileModel getByDemandId(Long demandId);

    List<InstallmentModel> getInstallments(Long creditFileId);

    Long resolveInstallmentId(UUID publicId);

    InstallmentModel declarePayment(Long demandId, Long installmentId, String sourceRib);

    InstallmentModel validatePayment(Long demandId, Long installmentId);

    InstallmentModel rejectPayment(Long demandId, Long installmentId, String reason);

    void sendOverdueReminders();
}
