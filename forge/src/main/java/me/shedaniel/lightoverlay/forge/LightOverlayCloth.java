package me.shedaniel.lightoverlay.forge;

import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.forge.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Locale;
import java.util.Optional;

public class LightOverlayCloth {
    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("key.lightoverlay.category");
            
            ConfigEntryBuilder eb = builder.getEntryBuilder();
            ConfigCategory general = builder.getOrCreateCategory("config.lightoverlay.general");
            general.addEntry(eb.startTextDescription("§7" + I18n.format("description.lightoverlay.caching")).build());
            general.addEntry(eb.startBooleanToggle("config.lightoverlay.caching", LightOverlayClient.caching).setDefaultValue(false).setSaveConsumer(bool -> LightOverlayClient.caching = bool).build());
            general.addEntry(eb.startIntSlider("config.lightoverlay.reach", LightOverlayClient.reach, 1, 64).setDefaultValue(12).setTextGetter(integer -> "Reach: " + integer + " Blocks").setSaveConsumer(integer -> LightOverlayClient.reach = integer).build());
            IntegerSliderEntry crossLevel = eb.startIntSlider("config.lightoverlay.crossLevel", LightOverlayClient.crossLevel, 0, 15).setDefaultValue(7).setTextGetter(integer -> "Cross Level: " + integer).setSaveConsumer(integer -> LightOverlayClient.crossLevel = integer).build();
            general.addEntry(crossLevel);
            general.addEntry(eb.startIntSlider("config.lightoverlay.secondaryLevel", LightOverlayClient.secondaryLevel, -1, 15)
                    .setErrorSupplier(integer -> {
                        if (integer >= 0 && integer >= crossLevel.getValue()) return Optional.of("Secondary Level cannot be higher than Cross Level!");
                        return Optional.empty();
                    }).setDefaultValue(-1).setTextGetter(integer -> integer < 0 ? "Off" : "Level: " + integer).setSaveConsumer(integer -> LightOverlayClient.secondaryLevel = integer).build());
            general.addEntry(eb.startBooleanToggle("config.lightoverlay.showNumber", LightOverlayClient.showNumber).setDefaultValue(false).setSaveConsumer(bool -> LightOverlayClient.showNumber = bool).build());
            general.addEntry(eb.startBooleanToggle("config.lightoverlay.smoothLines", LightOverlayClient.smoothLines).setDefaultValue(true).setSaveConsumer(bool -> LightOverlayClient.smoothLines = bool).build());
            general.addEntry(eb.startBooleanToggle("config.lightoverlay.underwater", LightOverlayClient.underwater).setDefaultValue(false).setSaveConsumer(bool -> LightOverlayClient.underwater = bool).build());
            general.addEntry(eb.startIntSlider("config.lightoverlay.lineWidth", MathHelper.floor(LightOverlayClient.lineWidth * 100), 100, 700).setDefaultValue(100).setTextGetter(integer -> "Light Width: " + LightOverlayClient.FORMAT.format(integer / 100d)).setSaveConsumer(integer -> LightOverlayClient.lineWidth = integer / 100f).build());
            general.addEntry(eb.startStrField("config.lightoverlay.yellowColor", "#" + toStringColor(LightOverlayClient.yellowColor))
                    .setDefaultValue("#FFFF00")
                    .setSaveConsumer(str -> LightOverlayClient.yellowColor = toIntColor(str))
                    .setErrorSupplier(s -> {
                        if (!s.startsWith("#") || s.length() != 7 || !isInt(s.substring(1)))
                            return Optional.of(I18n.format("config.lightoverlay.invalidColor"));
                        else return Optional.empty();
                    })
                    .build()
            );
            general.addEntry(eb.startStrField("config.lightoverlay.redColor", "#" + toStringColor(LightOverlayClient.redColor))
                    .setDefaultValue("#FF0000")
                    .setSaveConsumer(str -> LightOverlayClient.redColor = toIntColor(str))
                    .setErrorSupplier(s -> {
                        if (!s.startsWith("#") || s.length() != 7 || !isInt(s.substring(1)))
                            return Optional.of(I18n.format("config.lightoverlay.invalidColor"));
                        else return Optional.empty();
                    })
                    .build()
            );
            general.addEntry(eb.startStrField("config.lightoverlay.secondaryColor", "#" + toStringColor(LightOverlayClient.secondaryColor))
                    .setDefaultValue("#0000FF")
                    .setSaveConsumer(str -> LightOverlayClient.secondaryColor = toIntColor(str))
                    .setErrorSupplier(s -> {
                        if (!s.startsWith("#") || s.length() != 7 || !isInt(s.substring(1)))
                            return Optional.of(I18n.format("config.lightoverlay.invalidColor"));
                        else return Optional.empty();
                    })
                    .build()
            );
            
            return builder.setSavingRunnable(() -> {
                try {
                    LightOverlayClient.saveConfig(LightOverlayClient.configFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LightOverlayClient.loadConfig(LightOverlayClient.configFile);
            }).build();
        });
    }
    
    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static int toIntColor(String str) {
        String substring = str.substring(1);
        int r = Integer.parseInt(substring.substring(0, 2), 16);
        int g = Integer.parseInt(substring.substring(2, 4), 16);
        int b = Integer.parseInt(substring.substring(4, 6), 16);
        return (r << 16) + (g << 8) + b;
    }
    
    private static String toStringColor(int toolColor) {
        String r = Integer.toHexString((toolColor >> 16) & 0xFF);
        String g = Integer.toHexString((toolColor >> 8) & 0xFF);
        String b = Integer.toHexString((toolColor >> 0) & 0xFF);
        if (r.length() == 1)
            r = "0" + r;
        if (g.length() == 1)
            g = "0" + g;
        if (b.length() == 1)
            b = "0" + b;
        return (r + g + b).toUpperCase(Locale.ROOT);
    }
}
