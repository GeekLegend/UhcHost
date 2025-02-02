package fr.geeklegend;

import fr.geeklegend.command.manager.CommandManager;
import fr.geeklegend.config.ConfigManager;
import fr.geeklegend.inventory.manager.InventoryManager;
import fr.geeklegend.listener.manager.ListenerManager;
import fr.geeklegend.scoreboard.ScoreboardManager;
import fr.geeklegend.server.ServerSettings;
import fr.geeklegend.game.GameManager;
import fr.geeklegend.game.GameState;
import fr.geeklegend.util.Constant;
import fr.geeklegend.world.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends JavaPlugin
{
    @Getter
    private static Main plugin;

    @Getter
    private ServerSettings serverSettings;

    @Getter
    private ConfigManager configManager;

    @Getter
    private GameManager gameManager;

    @Getter
    private InventoryManager inventoryManager;

    @Getter
    private ScheduledExecutorService executorMonoThread;

    @Getter
    private ScheduledExecutorService scheduledExecutorService;

    @Getter
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable()
    {
        plugin = this;

        configManager = new ConfigManager(this);

        serverSettings = new ServerSettings();
        serverSettings.loadHosts();
        serverSettings.loadBans();

        gameManager = new GameManager();
        inventoryManager = new InventoryManager(this);

        executorMonoThread = Executors.newScheduledThreadPool(16);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager();

        new CommandManager(this);
        new ListenerManager(this);
    }

    @Override
    public void onDisable()
    {
        WorldManager worldManager = gameManager.getWorldManager();

        plugin = null;

        serverSettings.saveBans();

        scoreboardManager.onDisable();

        if (gameManager.getGameState().equals(GameState.FINISH))
        {
            Bukkit.unloadWorld(WorldManager.WORLD.getName(), false);

            worldManager.deleteWorld(new File(WorldManager.WORLD.getName()));
        }
    }
}
