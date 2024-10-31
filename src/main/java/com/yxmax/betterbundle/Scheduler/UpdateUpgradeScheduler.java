package com.yxmax.betterbundle.Scheduler;

import com.google.gson.Gson;
import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.UUID;

import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class UpdateUpgradeScheduler implements Runnable{

    int Bundle_ID;

    int less_coin;

    int menu_level;

    Player player;

    public UpdateUpgradeScheduler(Player player, int Bundle_ID, int less_coin,int menu_level){
        this.Bundle_ID = Bundle_ID;
        this.less_coin = less_coin;
        this.player = player;
        this.menu_level = menu_level;
    }
    @Override
    public void run() {
        try {
            sendDebug(player.getName() + " upgrade the bundle " + Bundle_ID + " to level: " + menu_level);
            DataBases.UpdateCoin(con, String.valueOf(player.getUniqueId()), less_coin);
            DataBases.UpdateLevel(con, Bundle_ID, menu_level);
            updateCoin(player);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
