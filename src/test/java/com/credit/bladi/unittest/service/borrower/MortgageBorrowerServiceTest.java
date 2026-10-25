package com.credit.bladi.unittest.service.borrower;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_SALMA_AT_GMAIL_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_MORTGAGE7;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SALMA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.EnterpriseUserLinkException;
import com.credit.bladi.exception.EnterpriseUserLinkException.EnterpriseUserLinkExceptionType;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.borrower.MortgageBorrowerModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.borrower.IMortgageBorrowerRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.borrower.MortgageBorrowerService;
import com.credit.bladi.service.invitation.IClientInvitationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageBorrowerServiceTest {
    @Mock
    private IMortgageBorrowerRepository borrowerRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IClientInvitationService invitationService;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private MortgageBorrowerService service;

    @BeforeEach
    void setUp() {
        service = new MortgageBorrowerService(borrowerRepository, userRepository, invitationService, auditService,
            currentActorProvider);
    }

    private static DemandModel mortgage(String contactEmail) {
        return MockDemand.mortgage(7L, UUID.randomUUID(), LABEL_DEM_MORTGAGE7, 2L, PERSON_SALMA_IDRISSI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, contactEmail, null, null,
            ProductType.CONVENTIONAL, CspCode.SALA, Money.of(AMOUNT_600000),
            Money.of(AMOUNT_20000), 35, false, null);
    }

    private static User client(Long id, String username) {
        return User.getBuilder().id(id).username(username).role(UserRole.CLIENT).build();
    }

    @Test
    void shouldLinkTheOnlyClientAccountUsingTheApplicantEmail() {
        when(borrowerRepository.findByDemandId(7L)).thenReturn(Optional.empty());
        when(userRepository.findClientsByEmail(EMAIL_SALMA_AT_GMAIL_COM)).thenReturn(List.of(client(9L, USER_SALMA)));

        service.resolveBorrowerForClassification(mortgage(EMAIL_SALMA_AT_GMAIL_COM), USER_ANALYST1);

        verify(borrowerRepository).link(7L, 9L, BorrowerLinkOrigin.AUTO_MATCHED_ON_CLASSIFICATION, USER_ANALYST1);
        verify(auditService).log(eq(USER_ANALYST1), eq(AuditAction.MORTGAGE_BORROWER_LINKED), any(AuditTarget.class),
            anyString());
        verifyNoInteractions(invitationService);
    }

    @Test
    void shouldInviteTheApplicantWhenNoClientAccountExists() {
        when(borrowerRepository.findByDemandId(7L)).thenReturn(Optional.empty());
        when(userRepository.findClientsByEmail(EMAIL_SALMA_AT_GMAIL_COM)).thenReturn(List.of());

        service.resolveBorrowerForClassification(mortgage(EMAIL_SALMA_AT_GMAIL_COM), USER_ANALYST1);

        verify(invitationService).createBorrowerInvitation(eq(7L), eq(EMAIL_SALMA_AT_GMAIL_COM), any(),
            eq(USER_ANALYST1));
        verify(borrowerRepository, never()).link(any(), any(), any(), any());
    }

    @Test
    void shouldLinkNobodyWhenSeveralClientAccountsShareTheEmail() {
        when(borrowerRepository.findByDemandId(7L)).thenReturn(Optional.empty());
        when(userRepository.findClientsByEmail(EMAIL_SALMA_AT_GMAIL_COM))
            .thenReturn(List.of(client(9L, USER_SALMA), client(10L, "salma2")));

        service.resolveBorrowerForClassification(mortgage(EMAIL_SALMA_AT_GMAIL_COM), USER_ANALYST1);

        verify(borrowerRepository, never()).link(any(), any(), any(), any());
        verifyNoInteractions(invitationService);
    }

    @Test
    void shouldKeepAnExistingBorrowerAndSkipDossiersWithoutEmail() {
        when(borrowerRepository.findByDemandId(7L)).thenReturn(Optional.of(
            new MortgageBorrowerModel(7L, 9L, BorrowerLinkOrigin.EMAIL_INVITATION, USER_ANALYST1, Instant.now())));

        service.resolveBorrowerForClassification(mortgage(EMAIL_SALMA_AT_GMAIL_COM), USER_ANALYST1);
        service.resolveBorrowerForClassification(mortgage(" "), USER_ANALYST1);

        verifyNoInteractions(userRepository, invitationService);
    }

    @Test
    void shouldLetTheBorrowerThroughAndStopAnyoneElse() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SALMA);
        when(userRepository.findByUsername(USER_SALMA)).thenReturn(Optional.of(client(9L, USER_SALMA)));
        when(borrowerRepository.isBorrower(7L, 9L)).thenReturn(true);
        when(borrowerRepository.isBorrower(8L, 9L)).thenReturn(false);

        service.assertCurrentUserIsBorrower(7L);
        EnterpriseUserLinkException ex = assertThrows(EnterpriseUserLinkException.class,
            () -> service.assertCurrentUserIsBorrower(8L));

        assertEquals(EnterpriseUserLinkExceptionType.DEMAND_NOT_LINKED, ex.getType());
    }

    @Test
    void shouldListTheDossiersOfTheCurrentClient() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SALMA);
        when(userRepository.findByUsername(USER_SALMA)).thenReturn(Optional.of(client(9L, USER_SALMA)));
        when(borrowerRepository.findDemandIdsByUserId(9L)).thenReturn(List.of(7L, 3L));

        assertEquals(List.of(7L, 3L), service.getMyDemandIds());
    }
}
