package com.credit.bladi.perf;

import static com.credit.bladi.perf.Config.BASE_URL;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.http.HttpDsl.http;

import com.credit.bladi.perf.scenarios.admin.AdminResourceScenario;
import com.credit.bladi.perf.scenarios.audit.AuditLogScenario;
import com.credit.bladi.perf.scenarios.auth.AuthSessionScenario;
import com.credit.bladi.perf.scenarios.auth.LoginScenario;
import com.credit.bladi.perf.scenarios.auth.RegisterScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.FoncMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.IndeMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.MreMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.ProfMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.RetrMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.mortgage.csp.SalaMortgageCreditScenario;
import com.credit.bladi.perf.scenarios.user.UserReadScenario;
import com.credit.bladi.perf.utils.BamParametersUtils;
import com.credit.bladi.perf.utils.CreditRateUtils;
import com.credit.bladi.perf.utils.MourabahaParametersUtils;
import com.credit.bladi.perf.utils.SourceFile;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.ZonedDateTime;

public class CreditBladiStressTest extends Simulation {
    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .header("X-Forwarded-For", "#{clientIp}");

    private final SalaMortgageCreditScenario sala = new SalaMortgageCreditScenario();
    private final FoncMortgageCreditScenario fonc = new FoncMortgageCreditScenario();
    private final RetrMortgageCreditScenario retr = new RetrMortgageCreditScenario();
    private final ProfMortgageCreditScenario prof = new ProfMortgageCreditScenario();
    private final IndeMortgageCreditScenario inde = new IndeMortgageCreditScenario();
    private final MreMortgageCreditScenario mre = new MreMortgageCreditScenario();

    private final LoginScenario login = new LoginScenario();
    private final RegisterScenario register = new RegisterScenario();
    private final AuthSessionScenario authSession = new AuthSessionScenario();

    private final AdminResourceScenario bamRead = new AdminResourceScenario(
        "BAM Parameters Read (manager)",
        SourceFile.BAM_PAGINATION_SOURCE, BamParametersUtils.getAllParameters(), BamParametersUtils.getById(),
        SourceFile.BAM_PATCH_SOURCE, BamParametersUtils.patchFees());

    private final AdminResourceScenario creditRateRead = new AdminResourceScenario(
        "Credit Rates Read (manager)",
        SourceFile.CREDIT_RATE_PAGINATION_SOURCE, CreditRateUtils.getAll(), CreditRateUtils.getById(),
        SourceFile.CREDIT_RATE_PATCH_SOURCE, CreditRateUtils.patchRates());

    private final AdminResourceScenario mourabahaRead = new AdminResourceScenario(
        "Mourabaha Parameters Read (manager)",
        SourceFile.MOURABAHA_PAGINATION_SOURCE, MourabahaParametersUtils.getAll(),
        MourabahaParametersUtils.getById(),
        SourceFile.MOURABAHA_PATCH_SOURCE, MourabahaParametersUtils.patchRates());

    private final UserReadScenario userRead = new UserReadScenario();

    private final AuditLogScenario auditLog = new AuditLogScenario();

    {
        setUp(
            sala.simulateTest(5),

            fonc.simulateTest(3),

            retr.simulateTest(2),

            prof.simulateTest(2),

            inde.simulateTest(2),

            mre.simulateTest(1),

            login.loginTest(5),
            register.registerTest(1),
            authSession.authSessionTest(1),

            bamRead.test(2),

            creditRateRead.test(1),

            mourabahaRead.test(1),

            userRead.userReadTest(1),

            auditLog.auditLogTest(1)

        ).protocols(httpProtocol)
            .assertions(
                global().responseTime().percentile(95).lt(2000),
                global().successfulRequests().percent().gte(99.0)
        );
    }

    @Override
    public void before() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      Credit Bladi: Stress Test Start        ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("Target : " + BASE_URL);
        System.out.println("Start  : " + ZonedDateTime.now());
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  SALA         → 5 users/s  (ramp 30s + 2min constant)");
        System.out.println("  FONC         → 3 users/s  (ramp 30s + 2min constant)");
        System.out.println("  RETR         → 2 users/s  (ramp 30s + 2min constant)");
        System.out.println("  PROF         → 2 users/s  (ramp 30s + 2min constant)");
        System.out.println("  INDE         → 2 users/s  (ramp 30s + 2min constant)");
        System.out.println("  MRE          → 1 user/s   (ramp 30s + 2min constant)");
        System.out.println("  Login        → 5 users/s  (ramp 20s + 1min constant)");
        System.out.println("  Register     → 1 user/s   (ramp 20s + 1min constant)");
        System.out.println("  Auth session → 1 user/s   (ramp 20s + 1min constant) [me+refresh+logout]");
        System.out.println("  BAM          → 2 users/s  (ramp 10s + 2min constant) [list+detail+patch]");
        System.out.println("  Credit rates → 1 user/s   (ramp 10s + 2min constant) [list+detail+patch]");
        System.out.println("  Mourabaha    → 1 user/s   (ramp 10s + 2min constant) [list+detail+patch]");
        System.out.println("  Users        → 1 user/s   (ramp 10s + 2min constant) [list+detail+create+role]");
        System.out.println("  Audit log    → 1 user/s   (ramp 10s + 2min constant)");
    }

    @Override
    public void after() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      Credit Bladi: Stress Test Done         ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("End     : " + ZonedDateTime.now());
        System.out.println("Reports : target/gatling/");
    }
}
