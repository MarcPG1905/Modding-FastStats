package com.marcpg.faststats;

import com.marcpg.faststats.compat.PlatformManager;
import dev.faststats.Metrics;
import dev.faststats.SimpleContext;
import dev.faststats.Token;
import dev.faststats.config.SimpleConfig;
import dev.faststats.internal.LoggerFactory;
import dev.faststats.internal.PlatformLoggerFactory;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.concurrent.*;

@SuppressWarnings("UnstableApiUsage")
public final class ModContext extends SimpleContext {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "faststats-submitter");
        thread.setDaemon(true);
        return thread;
    });

    private final Set<Future<?>> tasks = new CopyOnWriteArraySet<>();
    private final String modId;

    private ModContext(Factory factory, LoggerFactory loggerFactory, String modId, @Token String token) {
        super(
                factory,
                loggerFactory,
                SimpleConfig.read(PlatformManager.compat().configDir().resolve("faststats").resolve("config.properties"), loggerFactory),
                PlatformManager.platform().name.toLowerCase(),
                token
        );

        this.modId = modId;

        initializeServices(factory);
        PlatformManager.compat().registerLifecycleEvents(this::ready, this::shutdown);
    }

    @Override
    @Contract(value = " -> new", pure = true)
    protected Metrics.@NonNull Factory metricsFactory() {
        return new ModMetrics.Factory(this, modId);
    }

    @Override
    protected boolean preSubmissionStart() {
        return ((SimpleConfig) getConfig()).preSubmissionStart(this);
    }

    @Override
    public @NonNull String getProjectName() {
        return PlatformManager.compat().modName();
    }

    @Override
    protected void scheduleAtFixedRate(@NonNull Runnable task, long initialDelay, long period, @NonNull TimeUnit unit) {
        tasks.add(executor.scheduleAtFixedRate(task, initialDelay, period, unit));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        tasks.forEach(task -> task.cancel(true));
        tasks.clear();
        executor.shutdown();
    }

    public static final class Factory extends SimpleContext.Factory<ModContext, Factory> {
        private final String modId;
        private final @Token String token;

        public Factory(String modId, @Token String token) {
            this.modId = modId;
            this.token = token;
        }

        @Override
        public @NonNull ModContext create() {
            PlatformManager.init(modId);

            org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(modId);
            PlatformLoggerFactory loggerFactory = new PlatformLoggerFactory((level, throwable, message) -> {
                switch (level) {
                    case INFO -> {
                        if (throwable == null) logger.info(message);
                        else logger.info(message, throwable);
                    }
                    case ERROR -> {
                        if (throwable == null) logger.error(message);
                        else logger.error(message, throwable);
                    }
                    case WARN -> {
                        if (throwable == null) logger.warn(message);
                        else logger.warn(message, throwable);
                    }
                }
            });
            return new ModContext(this, loggerFactory, modId, token);
        }
    }
}
