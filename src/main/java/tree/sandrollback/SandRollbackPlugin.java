package tree.sandrollback;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tree.sandrollback.commands.PluginReloadCommand;
import tree.sandrollback.listeners.SandBreakListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SandRollbackPlugin extends JavaPlugin {
    private long rollbackTime;
    private static SandRollbackPlugin instance;
    private SandBreakListener sandBreakListener;

    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        createDefaultConfig();
        FileConfiguration config = getConfig();
        rollbackTime = config.getLong("rollback-time", 5L) * 20;
        sandBreakListener = new SandBreakListener();
        getServer().getPluginManager().registerEvents(sandBreakListener, this);
        Objects.requireNonNull(getCommand("SandRollbackReload")).setExecutor(new PluginReloadCommand());
    }

    @Override
    public void onDisable() {
        if (sandBreakListener != null) {
            sandBreakListener.shutdown();
        }
        // Plugin shutdown logic
    }

    public static SandRollbackPlugin getInstance() {
        return instance;
    }

    public long getRollbackTime() {
        return rollbackTime;
    }

    private void createDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                config.set("rollback-time", 5L);
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reloadConfig();
    }
}
