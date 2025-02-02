package fr.geeklegend.scoreboard;

import fr.geeklegend.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager
{
    private final Map<UUID, PersonalScoreboard> scoreboards;
    private final ScheduledFuture glowingTask;
    private final ScheduledFuture reloadingTask;
    private int ipCharIndex;
    private int cooldown;

    public ScoreboardManager()
    {
        scoreboards = new HashMap<>();
        ipCharIndex = 0;
        cooldown = 0;

            glowingTask = Main.getPlugin().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            String ip = colorIpAt();
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getPlugin().getExecutorMonoThread().execute(() -> scoreboard.setLines(ip));
        }, 80, 80, TimeUnit.MILLISECONDS);

        reloadingTask = Main.getPlugin().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getPlugin().getExecutorMonoThread().execute(scoreboard::reloadData);
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void onDisable()
    {
        scoreboards.values().forEach(PersonalScoreboard::onLogout);
    }

    public void onLogin(Player player)
    {
        if (scoreboards.containsKey(player.getUniqueId()))
        {
            return;
        }
        scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player));
    }

    public void onLogout(Player player)
    {
        if (scoreboards.containsKey(player.getUniqueId()))
        {
            scoreboards.get(player.getUniqueId()).onLogout();
            scoreboards.remove(player.getUniqueId());
        }
    }

    public void update(Player player)
    {
        if (scoreboards.containsKey(player.getUniqueId()))
        {
            scoreboards.get(player.getUniqueId()).reloadData();
        }
    }

    private String colorIpAt()
    {
        String ip = "  Le monde de l'UHC";

        if (cooldown > 0)
        {
            cooldown--;
            return ChatColor.LIGHT_PURPLE + ip;
        }

        StringBuilder formattedIp = new StringBuilder();

        if (ipCharIndex > 0)
        {
            formattedIp.append(ip, 0, ipCharIndex - 1);
            formattedIp.append(ChatColor.LIGHT_PURPLE).append(ip, ipCharIndex - 1, ipCharIndex);
        } else
        {
            formattedIp.append(ip, 0, ipCharIndex);
        }

        formattedIp.append(ChatColor.YELLOW).append(ip.charAt(ipCharIndex));

        if (ipCharIndex + 1 < ip.length())
        {
            formattedIp.append(ChatColor.YELLOW).append(ip.charAt(ipCharIndex + 1));

            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.LIGHT_PURPLE).append(ip.substring(ipCharIndex + 2));

            ipCharIndex++;
        } else
        {
            ipCharIndex = 0;
            cooldown = 50;
        }

        return ChatColor.LIGHT_PURPLE + formattedIp.toString();
    }

}