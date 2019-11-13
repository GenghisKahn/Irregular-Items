package com.genghiskahn1992.irregularitems.utilites;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_FIRSTBLOCK = "firstblock";
    public static final String SUBCATEGORY_COMBUSTIONGENERATOR = "combustiongenerator";
    public static final String SUBCATEGORY_QUANTUMCAPACITOR = "quantumcapacirot";
    public static final String SUBCATEGORY_BASICQUANTUMGENERATOR = "quantumgenerator";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue FIRSTBLOCK_MAXPOWER;
    public static ForgeConfigSpec.IntValue FIRSTBLOCK_GENERATE;
    public static ForgeConfigSpec.IntValue FIRSTBLOCK_SEND;
    public static ForgeConfigSpec.IntValue FIRSTBLOCK_TICKS;

    public static ForgeConfigSpec.IntValue COMBUSTIONGENERATOR_MAXPOWER;
    public static ForgeConfigSpec.IntValue COMBUSTIONGENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue COMBUSTIONGENERATOR_SEND;
    public static ForgeConfigSpec.IntValue COMBUSTIONGENERATOR_TICKS;

    public static ForgeConfigSpec.IntValue QUANTUMCAPACITOR_MAXPOWER;
    public static ForgeConfigSpec.IntValue QUANTUMCAPACITOR_SEND;

    public static ForgeConfigSpec.IntValue BASICQUANTUMGENERATOR_MAXPOWER;
    public static ForgeConfigSpec.IntValue BASICQUANTUMGENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue BASICQUANTUMGENERATOR_SEND;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupFirstBlockConfig();
        setupCombustionGeneratorConfig();
        setupQuantumCapacitorConfig();
        setupBasicQuantumGeneratorConfig();

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupFirstBlockConfig(){
        COMMON_BUILDER.comment("FirstBlock settings").push(SUBCATEGORY_FIRSTBLOCK);

        FIRSTBLOCK_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the FirstBlock generator")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        FIRSTBLOCK_GENERATE = COMMON_BUILDER.comment("Power generation per diamond")
                .defineInRange("generate", 1000, 0, Integer.MAX_VALUE);
        FIRSTBLOCK_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);
        FIRSTBLOCK_TICKS = COMMON_BUILDER.comment("Ticks per diamond")
                .defineInRange("ticks", 20, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    private static void setupCombustionGeneratorConfig(){
        COMMON_BUILDER.comment("Combustion Generator").push(SUBCATEGORY_COMBUSTIONGENERATOR);

        COMBUSTIONGENERATOR_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the combustion generator")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        COMBUSTIONGENERATOR_GENERATE = COMMON_BUILDER.comment("Power generation per tick")
                .defineInRange("generate", 25, 0, Integer.MAX_VALUE);
        COMBUSTIONGENERATOR_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);
        COMBUSTIONGENERATOR_TICKS = COMMON_BUILDER.comment("Tick multiplier (Item Ticks = Furnace Burn time * multiplier)")
                .defineInRange("ticks", 1, 0, 10);

        COMMON_BUILDER.pop();
    }

    private static void setupQuantumCapacitorConfig(){
        COMMON_BUILDER.comment("Quantum Capacitor settings").push(SUBCATEGORY_QUANTUMCAPACITOR);

        QUANTUMCAPACITOR_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the Quantum Capacitor")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        QUANTUMCAPACITOR_SEND = COMMON_BUILDER.comment("Power to try to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    private static void setupBasicQuantumGeneratorConfig(){
        COMMON_BUILDER.comment("Basic Quantum Generator").push(SUBCATEGORY_BASICQUANTUMGENERATOR);

        BASICQUANTUMGENERATOR_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the combustion generator")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        BASICQUANTUMGENERATOR_GENERATE = COMMON_BUILDER.comment("Power generation per tick")
                .defineInRange("generate", 25, 0, Integer.MAX_VALUE);
        BASICQUANTUMGENERATOR_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path){

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent){

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent){

    }

}
