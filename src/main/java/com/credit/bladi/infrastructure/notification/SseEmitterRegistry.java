package com.credit.bladi.infrastructure.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRegistry {
    private static final Logger LOGGER = LogManager.getLogger(SseEmitterRegistry.class);

    private static final long EMITTER_TIMEOUT_MS = 30L * 60 * 1000;

    private final Map<String, List<SseEmitter>> emittersByUsername = new ConcurrentHashMap<>();

    public SseEmitter register(String username) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        List<SseEmitter> emitters = emittersByUsername.computeIfAbsent(username,
            key -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> remove(username, emitter));
        emitter.onTimeout(() -> remove(username, emitter));
        emitter.onError(e -> remove(username, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            remove(username, emitter);
        }
        return emitter;
    }

    public void send(String username, String eventName) {
        List<SseEmitter> emitters = emittersByUsername.get(username);
        if (emitters == null) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(eventName));
            } catch (IOException e) {
                remove(username, emitter);
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void heartbeat() {
        emittersByUsername.forEach((username, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("ping"));
                } catch (IOException e) {
                    remove(username, emitter);
                }
            }
        });
    }

    private void remove(String username, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByUsername.get(username);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByUsername.remove(username);
        }
        LOGGER.debug("SSE_EMITTER_REMOVED username={}", username);
    }
}
