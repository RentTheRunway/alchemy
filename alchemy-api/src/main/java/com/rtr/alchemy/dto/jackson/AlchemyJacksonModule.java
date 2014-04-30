package com.rtr.alchemy.dto.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * Encapsulates all Jackson modules needed to serialize/deserialize Alchemy DTOs properly
 */
public class AlchemyJacksonModule extends Module {
    private final GuavaModule guavaModule = new GuavaModule();
    private final MrBeanModule mrBeanModule = new MrBeanModule();
    private final JodaModule jodaModule = new JodaModule();

    @Override
    public String getModuleName() {
        return "AlchemyJacksonModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        guavaModule.setupModule(context);
        mrBeanModule.setupModule(context);
        jodaModule.setupModule(context);
    }
}
