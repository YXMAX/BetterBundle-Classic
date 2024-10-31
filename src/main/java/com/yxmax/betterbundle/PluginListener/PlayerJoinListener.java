package com.yxmax.betterbundle.PluginListener;

import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.PluginUtil.updateCoin;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) throws Exception {
        Player player = (Player) event.getPlayer();
        if(!DataBases.hasData(con, String.valueOf(player.getUniqueId()))){
            DataBases.CoinInsert(con, String.valueOf(player.getUniqueId()), 0,0);
        }
        updateCoin(player);
    }
}
