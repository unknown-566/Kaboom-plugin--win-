package com.example.kaboom;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class KaboomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("KaboomShutdown enabled");
        // Register command executor
        if (getCommand("kaboom") != null) {
            getCommand("kaboom").setExecutor(new KaboomCommand(this));
        } else {
            getLogger().severe("Command 'kaboom' not found. Check plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("KaboomShutdown disabled");
    }

    /** Convenience: run a console command safely */
    public void runConsole(String cmd) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, cmd);
    }
}
