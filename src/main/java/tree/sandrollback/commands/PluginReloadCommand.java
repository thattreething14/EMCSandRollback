package tree.sandrollback.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tree.sandrollback.SandRollbackPlugin;

public class PluginReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "[SandRollback]" + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        SandRollbackPlugin.getInstance().onDisable();
        SandRollbackPlugin.getInstance().onEnable();
        sender.sendMessage(ChatColor.GOLD + "[SandRollback]" + ChatColor.AQUA + " SandRollback plugin configuration reloaded.");
        return true;
    }
}
