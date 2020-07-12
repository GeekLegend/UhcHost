package fr.geeklegend.uhchost.listener;

import fr.geeklegend.uhchost.Main;
import fr.geeklegend.uhchost.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener
{
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        GameState gameState = Main.getPlugin().getGameManager().getGameState();

        if (!gameState.equals(GameState.GAME))
        {
            event.setCancelled(true);
        }
    }
}
