package com.credit.bladi.perf.scenarios.user;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.exitHereIfFailed;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import com.credit.bladi.perf.utils.AuthUtils;
import com.credit.bladi.perf.utils.ClientIpUtils;
import com.credit.bladi.perf.utils.SourceFile;
import com.credit.bladi.perf.utils.UserUtils;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;

public class UserReadScenario {

    private static final Duration RAMP_UP = Duration.ofSeconds(10);
    private static final Duration LOAD_TIME = Duration.ofMinutes(2);

    private final ScenarioBuilder userReadScenario = scenario("Users Read (manager)")
        .exec(ClientIpUtils.assignSyntheticClientIp())
        .exec(AuthUtils.loginAsAdmin())
        .feed(csv(SourceFile.USERS_PAGINATION_SOURCE).circular())
        .exec(UserUtils.getAll())
        .exec(UserUtils.getById())
        .feed(csv(SourceFile.USERS_CREATE_SOURCE).circular())
        .exec(UserUtils.create())
        .exec(exitHereIfFailed())
        .exec(UserUtils.updateRoleOfCreatedUser());

    public PopulationBuilder userReadTest(double usersPerSec) {
        return userReadScenario.injectOpen(
            rampUsersPerSec(0).to(usersPerSec).during(RAMP_UP),
            constantUsersPerSec(usersPerSec).during(LOAD_TIME)
        );
    }
}
