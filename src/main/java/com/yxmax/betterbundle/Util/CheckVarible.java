package com.yxmax.betterbundle.Util;

import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.entity.Player;

import static com.yxmax.betterbundle.SQL.DataBases.con;

public class CheckVarible {

    public static Integer PlayerCoinOutPut(Player player){
        try {
            int Coin = DataBases.GetCoin(con, String.valueOf(player.getUniqueId()));
            return Coin;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
