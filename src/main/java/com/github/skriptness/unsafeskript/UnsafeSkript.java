package com.github.skriptness.unsafeskript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.skriptness.unsafeskript.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class UnsafeSkript extends JavaPlugin {

    private static final int PLUGIN_ID = 19569;
    private static UnsafeSkript instance;
    private static SkriptAddon addonInstance;
    public static boolean secSwapCodeUsed = false;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("Skript")) {
            getLogger().severe("Skript could not be found. Disabling unsafe-skript...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            addonInstance = Skript.registerAddon(getInstance()).setLanguageFileDirectory("lang");
            addonInstance.loadClasses("com.github.skriptness.unsafeskript.elements");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Metrics metrics = new Metrics(this, PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SimplePie("skriptVersion", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new Metrics.SimplePie("secSwapCodeUsed", () -> secSwapCodeUsed + ""));

    }

    public static UnsafeSkript getInstance() {
        return instance;
    }

    public static SkriptAddon getAddonInstance() {
        return addonInstance;
    }

}
