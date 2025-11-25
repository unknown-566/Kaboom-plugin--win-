package com.example.kaboom;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KaboomCommand implements CommandExecutor {

    private final KaboomPlugin plugin;
    // Delay before OS shutdown (seconds). Windows will show a system timer.
    private static final int SHUTDOWN_DELAY_SECONDS = 10;

    public KaboomCommand(KaboomPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only OP players can run it (no console)
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly OP players can run this command.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.isOp()) {
            p.sendMessage("§cYou must be OP to use /kaboom.");
            return true;
        }
        // Optional tiny confirm safeguard: require typing exactly /kaboom
        // (No extra args accepted to keep it simple.)
        if (args.length != 0) {
            p.sendMessage("§eUsage: §f/kaboom");
            return true;
        }

        // 1) Force save all worlds safely
        plugin.runConsole("save-all flush");

        // 2) Schedule Windows shutdown first so it still executes after server stops.
        //    Using a delay gives the server time to stop cleanly.
        try {
            String[] cmd = new String[] { "cmd", "/c", "shutdown", "/s", "/t", String.valueOf(SHUTDOWN_DELAY_SECONDS) };
            Runtime.getRuntime().exec(cmd);
            p.sendMessage("§aSystem shutdown scheduled in " + SHUTDOWN_DELAY_SECONDS + " seconds.");
        } catch (Exception e) {
            p.sendMessage("§cFailed to schedule Windows shutdown: " + e.getMessage());
            plugin.getLogger().severe("Failed to run shutdown: " + e.getMessage());
            return true;
        }

        // 3) Broadcast and stop the server
        Bukkit.broadcastMessage("§cServer is stopping now. Windows will power off in " + SHUTDOWN_DELAY_SECONDS + "s.");
        // Stop after a short tick delay so the broadcast flushes.
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.runConsole("stop"), 20L); // 1 second

        return true;
    }
}
