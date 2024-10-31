package com.yxmax.betterbundle.PlaceHolder;

import com.yxmax.betterbundle.BetterBundle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.yxmax.betterbundle.BetterBundle.BundleCoinMap;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private BetterBundle plugin;

    public PlaceholderExpansion(BetterBundle plugin){
        this.plugin = plugin;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "betterbundle";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YXMAX";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(identifier.equals("coin")){
            return String.valueOf(BundleCoinMap.get(player.getUniqueId()));
        }
        return null;
    }
}
