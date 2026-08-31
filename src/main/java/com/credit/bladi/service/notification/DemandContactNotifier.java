package com.credit.bladi.service.notification;

import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.enterprise.IApprovedEnterpriseLinksProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DemandContactNotifier {
    private static final String PORTAL_HOME = "/portal";

    private final IApprovedEnterpriseLinksProvider enterpriseUserLinkService;

    private final IUserRepository userRepository;

    private final IEnterpriseRepository enterpriseRepository;

    private final INotificationService notificationService;

    public DemandContactNotifier(IApprovedEnterpriseLinksProvider enterpriseUserLinkService,
            IUserRepository userRepository, IEnterpriseRepository enterpriseRepository,
            INotificationService notificationService) {
        this.enterpriseUserLinkService = enterpriseUserLinkService;
        this.userRepository = userRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.notificationService = notificationService;
    }

    public void notify(DemandModel demand, NotificationType type, LocalizedText title, LocalizedText body) {
        notify(demand, demand.enterpriseIdOrNull(), type, title, body);
    }

    public void notify(DemandModel demand, Long enterpriseId, NotificationType type, LocalizedText title,
            LocalizedText body) {
        String link = portalLink(demand, enterpriseId);
        List<EnterpriseUserLinkModel> approvedLinks = enterpriseId != null
            ? enterpriseUserLinkService.listApprovedForEnterprise(enterpriseId)
            : List.of();
        if (approvedLinks.isEmpty()) {
            notificationService.notify(new NotificationMessage(null, demand.contactEmail(), demand.contactPhone(),
                demand.contactLanguage(), type, title, body, link));
            return;
        }
        Map<Long, User> clientsById = userRepository
            .findAllById(approvedLinks.stream().map(EnterpriseUserLinkModel::userId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));
        for (EnterpriseUserLinkModel approvedLink : approvedLinks) {
            User client = clientsById.get(approvedLink.userId());
            if (client != null) {
                Language language = client.getPreferredLanguage() != null
                    ? client.getPreferredLanguage()
                    : demand.contactLanguage();
                notificationService.notify(new NotificationMessage(client.getUsername(), client.getEmail(),
                    demand.contactPhone(), language, type, title, body, link));
            }
        }
    }

    private String portalLink(DemandModel demand, Long enterpriseId) {
        if (enterpriseId == null) {
            return PORTAL_HOME;
        }
        return enterpriseRepository.findById(enterpriseId)
            .map(EnterpriseModel::publicId)
            .map(enterprisePublicId -> PORTAL_HOME + "/" + enterprisePublicId + "/demands/" + demand.publicId())
            .orElse(PORTAL_HOME);
    }
}
