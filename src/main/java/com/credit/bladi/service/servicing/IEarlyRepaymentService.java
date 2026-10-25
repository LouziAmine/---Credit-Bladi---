package com.credit.bladi.service.servicing;

import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import com.credit.bladi.model.vo.Money;
import java.util.List;

public interface IEarlyRepaymentService {
    EarlyRepaymentQuote quote(Long demandId, Money principal);

    EarlyRepaymentModel repay(Long demandId, Money principal);

    List<EarlyRepaymentModel> history(Long demandId);
}
