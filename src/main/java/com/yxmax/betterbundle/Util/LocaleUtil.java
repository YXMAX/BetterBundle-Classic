package com.yxmax.betterbundle.Util;

import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.yxmax.betterbundle.BetterBundle.LocaleConfig;
import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.CheckVarible.PlayerCoinOutPut;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class LocaleUtil {

    public static String bundlename(){
        String v = LocaleConfig.getString("Bundle.Display.Name");
        return v;
    }

    public static ArrayList<String> bundlelore(int id){
        ArrayList<String> bundlelorelist = new ArrayList<String>();
        List<String> bundlelist = LocaleConfig.getStringList("Bundle.Display.Lore");
        for(String s : bundlelist){
            bundlelorelist.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        bundlelorelist.add(ChatColor.translateAlternateColorCodes('&', " "));
        bundlelorelist.add(ChatColor.translateAlternateColorCodes('&', "&bID: &e" + id));
        return bundlelorelist;
    }

    public static String upgradetitle(){
        String v = LocaleConfig.getString("GUI.Upgrade.Title");
        return v;
    }

    public static String Bundle_Barrier_Name(){
        String v = LocaleConfig.getString("GUI.Bundle.Locked");
        return v;
    }

    public static String slotuptitle(int page){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Title").replaceAll("%upgrade_page%", String.valueOf(page));
        return v;
    }

    public static String slotupbundlename(Player player){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Bundle.Name").replaceAll("%betterbundle_coin%", String.valueOf(PlayerCoinOutPut(player)));
        return v;
    }

    public static String SlotUp_Locked_Name(){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Locked.Name");
        return v;
    }

    public static ArrayList<String> SlotUp_Locked_Lore(Integer level){
        int require_coin = 0;
        Boolean Slotup_judge = SlotUp_Mode_NormalJudge();
        int Slotup_value = SlotUp_Value();
        if(Slotup_judge){
            require_coin = level+Slotup_value-1;
        } else {
            require_coin = Slotup_value;
        }
        ArrayList<String> bundlelorelist = new ArrayList<String>();
        List<String> bundlelist = LocaleConfig.getStringList("GUI.Slotupgrade.Locked.Lore");
        for(String s : bundlelist){
            bundlelorelist.add(color(s).replaceAll("%upgrade_level%", String.valueOf(level)).replaceAll("%pre_level%", String.valueOf(level-1)).replaceAll("%require_coin%", String.valueOf(require_coin)));
        }
        return bundlelorelist;
    }

    public static String SlotUp_Unlocked_Name(){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Unlocked.Name");
        return v;
    }

    public static ArrayList<String> SlotUp_Unlocked_Lore(Integer level){
        int require_coin = 0;
        Boolean Slotup_judge = SlotUp_Mode_NormalJudge();
        int Slotup_value = SlotUp_Value();
        if(Slotup_judge){
            require_coin = level+Slotup_value-1;
        } else {
            require_coin = Slotup_value;
        }
        ArrayList<String> bundlelorelist = new ArrayList<String>();
        List<String> bundlelist = LocaleConfig.getStringList("GUI.Slotupgrade.Unlocked.Lore");
        for(String s : bundlelist){
            bundlelorelist.add(color(s).replaceAll("%require_coin%", String.valueOf(require_coin)));
        }
        return bundlelorelist;
    }

    public static String SlotUp_Upgraded_Name(){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Upgraded.Name");
        return v;
    }

    public static ArrayList<String> SlotUp_Upgraded_Lore(){
        ArrayList<String> bundlelorelist = new ArrayList<String>();
        List<String> bundlelist = LocaleConfig.getStringList("GUI.Slotupgrade.Upgraded.Lore");
        for(String s : bundlelist){
            bundlelorelist.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return bundlelorelist;
    }

    public static ArrayList<String> slotupbundlelore(){
        ArrayList<String> bundlelorelist = new ArrayList<String>();
        List<String> bundlelist = LocaleConfig.getStringList("GUI.Slotupgrade.Bundle.Lore");
        for(String s : bundlelist){
            bundlelorelist.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return bundlelorelist;
    }

    public static String SlotUp_Previous_Name(){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Previous.Name");
        return v;
    }

    public static String SlotUp_Next_Name(){
        String v = LocaleConfig.getString("GUI.Slotupgrade.Next.Name");
        return v;
    }

    public static String Upgradable_redirect(String bool){
        if(bool.equalsIgnoreCase("true")){
            return LocaleConfig.getString("Manage_menu.Upgrade_status.set_true");
        } else {
            return LocaleConfig.getString("Manage_menu.Upgrade_status.set_false");
        }
    }

    public static String bundletitle(int id){
        String v =LocaleConfig.getString("GUI.Bundle.Title").replaceAll("%bundle_id%", String.valueOf(id));
        return v;
    }

}
