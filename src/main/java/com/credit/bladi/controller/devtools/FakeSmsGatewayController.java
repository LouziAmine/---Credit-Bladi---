package com.credit.bladi.controller.devtools;

import static com.credit.bladi.common.PiiMasker.maskPhone;

import com.credit.bladi.infrastructure.sms.FakeSmsEntry;
import com.credit.bladi.infrastructure.sms.FakeSmsInbox;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/api/v1/fake-sms-gateway")
public class FakeSmsGatewayController {
    private static final Logger LOGGER = LogManager.getLogger(FakeSmsGatewayController.class);

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    private final FakeSmsInbox inbox;

    private final Clock clock;

    public FakeSmsGatewayController(FakeSmsInbox inbox, Clock clock) {
        this.clock = clock;
        this.inbox = inbox;
    }

    public record SendRequest(String to, String from, String message) {
    }

    @PostMapping("/sms")
    public ResponseEntity<Void> receive(@RequestBody SendRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        LOGGER.info("FAKE_SMS_GATEWAY_RECEIVED to={} from={}", maskPhone(request.to()), request.from());
        inbox.add(request.to(), request.from(), request.message(), authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/inbox", produces = MediaType.TEXT_HTML_VALUE)
    public String inbox() {
        List<FakeSmsEntry> entries = inbox.list();
        String body = entries.isEmpty() ? emptyState() : table(entries);
        return PAGE_TEMPLATE.formatted(body);
    }

    private String table(List<FakeSmsEntry> entries) {
        StringBuilder rows = new StringBuilder();
        Instant freshCutoff = clock.instant().minusSeconds(10);
        for (FakeSmsEntry entry : entries) {
            boolean fresh = entry.receivedAt().isAfter(freshCutoff);
            rows.append("""
                <tr>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s%s</td>
                  <td class="cb-fake-sms-auth">%s</td>
                </tr>
                """.formatted(
                    TIME_FORMAT.format(entry.receivedAt()),
                    escape(entry.to()),
                    escape(entry.from()),
                    fresh ? "<span class=\"cb-badge cb-badge--green\">"
                        + "<i class=\"bi bi-check-circle-fill\"></i>New</span> " : "",
                    escape(entry.message()),
                    escape(entry.authorizationHeader())));
        }
        return """
            <div class="cb-card cb-animate">
              <div class="cb-card__header cb-card__header--green">
                <span class="cb-card__header-icon cb-card__header-icon--green"><i class="bi bi-chat-dots"></i></span>
                <div>
                  <p class="cb-card__header-title">Messages received</p>
                  <p class="cb-card__header-sub">%d message(s): auto-refreshes every 3 seconds</p>
                </div>
              </div>
              <div class="cb-card__body--flush">
                <table class="cb-table">
                  <thead>
                    <tr><th>Received at</th><th>Recipient</th><th>Sender</th><th>Message</th>\
            <th>Authorization</th></tr>
                  </thead>
                  <tbody>
            %s\
                  </tbody>
                </table>
              </div>
            </div>
            """.formatted(entries.size(), rows);
    }

    private String emptyState() {
        return """
            <div class="cb-card cb-animate">
              <div class="cb-card__body">
                <div class="cb-empty">
                  <span class="cb-empty__icon"><i class="bi bi-chat-dots"></i></span>
                  <p class="cb-empty__title">No SMS received yet</p>
                  <p class="cb-empty__sub">Classify a file or trigger a client notification \
            to see a message appear here.</p>
                </div>
              </div>
            </div>
            """;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final String PAGE_TEMPLATE = """
        <!doctype html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <meta http-equiv="refresh" content="3">
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <title>Fake SMS Gateway: Credit Bladi</title>
          <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap">
          <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
          <style>
            :root {
              --cb-green: #006233; --cb-green-dark: #004d27; --cb-green-mid: #1a7a45;
              --cb-green-light: #e8f5ee; --cb-green-border: #b3d9c4;
              --cb-bg: #f4f7f5; --cb-surface: #ffffff; --cb-text: #1a2e22; --cb-text-light: #607364;
              --cb-border: #d0e0d8; --cb-shadow: 0 2px 16px rgba(0,98,51,.09);
              --cb-radius: 14px; --cb-radius-sm: 8px; --cb-radius-pill: 50px;
            }
            * { box-sizing: border-box; }
            body { font-family: 'Poppins', 'Segoe UI', system-ui, sans-serif; background: var(--cb-bg); \
        color: var(--cb-text); margin: 0; }
            .cb-hero {
              background: linear-gradient(135deg, var(--cb-green-dark) 0%%, var(--cb-green) 65%%, \
        var(--cb-green-mid) 100%%);
              color: #fff; padding: 1.75rem 1.5rem 1.5rem;
            }
            .cb-hero__eyebrow { display: flex; align-items: center; gap: .5rem; font-size: .8rem; \
        font-weight: 500; opacity: .7; margin-bottom: .4rem; }
            .cb-hero__title { font-size: clamp(1.3rem, 3vw, 1.9rem); font-weight: 700; margin: 0 0 .3rem; }
            .cb-hero__subtitle { font-size: .9rem; opacity: .72; margin: 0; }
            .cb-page { max-width: 1000px; margin: 0 auto; padding: 1.75rem 1.5rem 2.5rem; }
            .cb-card {
              background: var(--cb-surface); border: 1px solid var(--cb-border); border-radius: var(--cb-radius);
              box-shadow: var(--cb-shadow); overflow: hidden;
            }
            .cb-card__header {
              padding: 1rem 1.5rem; display: flex; align-items: center; gap: .75rem;
              border-bottom: 1px solid var(--cb-border);
            }
            .cb-card__header--green { background: var(--cb-green-light); }
            .cb-card__header-icon {
              width: 34px; height: 34px; border-radius: var(--cb-radius-sm);
              display: flex; align-items: center; justify-content: center; font-size: 1rem; flex-shrink: 0;
            }
            .cb-card__header-icon--green { background: var(--cb-green); color: #fff; }
            .cb-card__header-title { font-size: .95rem; font-weight: 700; color: var(--cb-text); margin: 0; }
            .cb-card__header-sub { font-size: .78rem; color: var(--cb-text-light); margin: 0; }
            .cb-card__body { padding: 1.5rem; }
            .cb-card__body--flush { padding: 0; overflow-x: auto; }
            .cb-badge {
              display: inline-flex; align-items: center; gap: .3rem; font-size: .75rem; font-weight: 600;
              padding: .28em .75em; border-radius: var(--cb-radius-pill);
            }
            .cb-badge--green { background: var(--cb-green-light); color: var(--cb-green); }
            .cb-table { width: 100%%; border-collapse: collapse; }
            .cb-table thead tr { background: var(--cb-green-light); }
            .cb-table thead th {
              padding: .7rem 1rem; font-size: .78rem; font-weight: 700; text-transform: uppercase;
              letter-spacing: .04em; color: var(--cb-green); border-bottom: 2px solid var(--cb-green-border);
              white-space: nowrap;
            }
            .cb-table tbody td {
              padding: .8rem 1rem; font-size: .875rem; border-bottom: 1px solid var(--cb-border);
              color: var(--cb-text); vertical-align: top;
            }
            .cb-table tbody tr:last-child td { border-bottom: none; }
            .cb-table tbody tr:hover td { background: var(--cb-bg); }
            .cb-fake-sms-auth { font-family: 'SFMono-Regular', Consolas, monospace; font-size: .78rem; \
        color: var(--cb-text-light); }
            .cb-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; \
        padding: 3.5rem 1rem; text-align: center; }
            .cb-empty__icon {
              width: 68px; height: 68px; border-radius: 50%%; background: var(--cb-green-light);
              color: var(--cb-green); display: flex; align-items: center; justify-content: center;
              font-size: 1.8rem; margin-bottom: 1rem;
            }
            .cb-empty__title { font-weight: 600; font-size: .95rem; color: var(--cb-text); margin-bottom: .35rem; }
            .cb-empty__sub { font-size: .82rem; color: var(--cb-text-light); margin: 0; max-width: 380px; }
            @keyframes cb-fadeUp { from { opacity: 0; transform: translateY(18px); } to { opacity: 1; \
        transform: translateY(0); } }
            .cb-animate { animation: cb-fadeUp .38s ease forwards; }
          </style>
        </head>
        <body>
          <div class="cb-hero">
            <div class="cb-hero__eyebrow"><i class="bi bi-tools"></i> DEVELOPMENT TOOL: DEV PROFILE</div>
            <h1 class="cb-hero__title"><i class="bi bi-phone"></i> Fake SMS Gateway</h1>
            <p class="cb-hero__subtitle">What <code>HttpSmsGateway</code> actually sent over HTTP: \
        the same HTTP client used in production, pointed here at localhost.</p>
          </div>
          <div class="cb-page">
        %s\
          </div>
        </body>
        </html>
        """;
}
