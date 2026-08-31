package com.credit.bladi.service.enterprise;

import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import java.util.List;

public interface IApprovedEnterpriseLinksProvider {
    List<EnterpriseUserLinkModel> listApprovedForEnterprise(Long enterpriseId);
}
