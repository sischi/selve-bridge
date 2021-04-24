package com.sischi.selvebridge.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationLogger implements HasLogger {

    private static final String APP_PROPERTY_PREFIX = "selvebridge";
    private static final List<String> PRINTABLE_PROPERTIES = Arrays.asList("server.port");

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        getLogger().info("====== Environment and configuration ======");
        getLogger().info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(prop -> (PRINTABLE_PROPERTIES.contains(prop) || prop.toLowerCase().startsWith(APP_PROPERTY_PREFIX)))
                .forEach(prop -> getLogger().info("{}: {}",
                    prop,
                    (prop.toLowerCase().contains("password") || prop.toLowerCase().contains("secret") ? "****" : env.getProperty(prop)))
                );
        getLogger().info("===========================================");
    }

}
