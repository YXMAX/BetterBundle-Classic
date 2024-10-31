package com.yxmax.betterbundle.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.yxmax.betterbundle.BetterBundle.LocaleConfig;
import static com.yxmax.betterbundle.Util.LocaleUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class MessageUtil {

    public static String nomode(){
        String v = LocaleConfig.getString("Messages.Nomode");
        return v;
    }

    public static String noupgrade(){
        String v = LocaleConfig.getString("Messages.Noupgrade");
        return v;
    }

    public static String noput(){
        String v = LocaleConfig.getString("Messages.Noputbundle");
        return v;
    }

    public static String noperm(){
        String v = LocaleConfig.getString("Messages.Nopermission");
        return v;
    }

    public static String nomaxslot(){
        String v = LocaleConfig.getString("Messages.Nomaxslot");
        return v;
    }

    public static String unknown(){
        String v = LocaleConfig.getString("Messages.Unknown");
        return v;
    }

    public static String massiveerror(){
        String v = LocaleConfig.getString("Messages.Massive_error");
        return v;
    }

    public static String maxsloterror(){
        String v = LocaleConfig.getString("Messages.Maxslot_error");
        return v;
    }

    public static String info_player_empty(){
        String v = LocaleConfig.getString("Messages.Info_player_empty");
        return v;
    }

    public static String info_fail(String owner){
        String v = LocaleConfig.getString("Messages.Info_fail").replaceAll("%player%", owner);
        return v;
    }

    public static String check_id_empty(){
        String v = LocaleConfig.getString("Messages.Check_id_empty");
        return v;
    }

    public static String check_not_id(){
        String v = LocaleConfig.getString("Messages.Check_not_id");
        return v;
    }

    public static String check_unknown_id(int id){
        String v = LocaleConfig.getString("Messages.Check_unknown_id").replaceAll("%check_id%", String.valueOf(id));
        return v;
    }

    public static String check_success(int id){
        String v = LocaleConfig.getString("Messages.Check_success").replaceAll("%check_id%", String.valueOf(id));
        return v;
    }

    public static String player_error(){
        String v = LocaleConfig.getString("Messages.Player_error");
        return v;
    }

    public static String reload(){
        String v = LocaleConfig.getString("Messages.Reload");
        return v;
    }

    public static String reload_menu(){
        String v = LocaleConfig.getString("Messages.Reload_menu");
        return v;
    }

    public static String onlyplayer(){
        String v = LocaleConfig.getString("Messages.Onlyplayer");
        return v;
    }

    public static String upgrade_false(){
        String v = LocaleConfig.getString("Messages.Upgrade_false");
        return v;
    }

    public static String nohelmet(){
        String v = LocaleConfig.getString("Messages.Noputhelmet");
        return v;
    }

    public static String upgrade_success(Integer level,int id){
        String v = LocaleConfig.getString("Messages.Upgrade_success").replaceAll("%bundle_id%", String.valueOf(id));
        return v.replaceAll("%upgrade_level%", String.valueOf(level));
    }

    public static String upgrade_fail(int id){
        String v = LocaleConfig.getString("Messages.Upgrade_fail").replaceAll("%bundle_id%", String.valueOf(id));
        return v;
    }

    public static String getBundleSuccess_Player(int id){
        String msg = LocaleConfig.getString("Messages.Get_bundle_success_player").replaceAll("%bundle_id%", String.valueOf(id));
        return msg;
    }

    public static String getBundleSuccess_Third(Player player,int id){
        String msg = LocaleConfig.getString("Messages.Get_bundle_success_third").replaceAll("%bundle_id%", String.valueOf(id));
        return msg.replaceAll("%player%", player.getName());
    }

    public static String openBundle_Msg(int id){
        String openmsg = LocaleConfig.getString("Messages.Open_bundle").replaceAll("%bundle_id%", String.valueOf(id));
        return openmsg;
    }

    public static String Info_title(String owner){
        String v = LocaleConfig.getString("Messages.Info_title").replaceAll("%player%", owner);
        return v;
    }

    public static String Give_error_type(){
        String v = LocaleConfig.getString("Messages.Give_error_type");
        return v;
    }

    public static String Give_error_onlineplayer(){
        String v = LocaleConfig.getString("Messages.Give_error_onlineplayer");
        return v;
    }

    public static String Give_error_player(){
        String v = LocaleConfig.getString("Messages.Give_error_player");
        return v;
    }

    public static String Give_not_id(){
        String v = LocaleConfig.getString("Messages.Give_not_id");
        return v;
    }

    public static String Give_not_num(){
        String v = LocaleConfig.getString("Messages.Give_not_num");
        return v;
    }

    public static String Give_id_not_exist(){
        String v = LocaleConfig.getString("Messages.Give_id_not_exist");
        return v;
    }

    public static String Give_bundle_success(Integer id, String player){
        String v = LocaleConfig.getString("Messages.Give_bundle_success").replaceAll("%bundle_id%", String.valueOf(id));
        return v.replaceAll("%player%", player);
    }

    public static String Give_bundle_player_msg(Integer id){
        String v = LocaleConfig.getString("Messages.Give_bundle_player_msg").replaceAll("%bundle_id%", String.valueOf(id));
        return v;
    }

    public static String Give_coin_success(Integer num,String player){
        String v = LocaleConfig.getString("Messages.Give_coin_success").replaceAll("%give_coin%", String.valueOf(num));
        return v.replaceAll("%player%", player);
    }

    public static String Give_coin_player_msg(Integer num){
        String v = LocaleConfig.getString("Messages.Give_coin_player_msg").replaceAll("%give_coin%", String.valueOf(num));
        return v;
    }

    public static String Info_detail(int id,int slot,Integer level,Integer maxslot,Boolean upgrade){
        String v = LocaleConfig.getString("Messages.Info_detail").replaceAll("%info_bundle_id%", String.valueOf(id)).replaceAll("%info_bundle_base_slot%", String.valueOf(slot));
        return v.replaceAll("%info_bundle_level%", String.valueOf(level)).replaceAll("%info_bundle_max_slot%", String.valueOf(maxslot)).replaceAll("%info_bundle_upgradable%", String.valueOf(upgrade));
    }

    public static String openUpgrade_Msg(int id){
        return LocaleConfig.getString("Messages.Open_upgrade_menu").replaceAll("%bundle_id%", String.valueOf(id));
    }

    public static String bundleNotExist(){
        return LocaleConfig.getString("Messages.Bundle_not_exist");
    }

    public static String bundleOccupy(){
        return LocaleConfig.getString("Messages.Bundle_occupy");
    }

    public static String upgrade_not_item(){
        return LocaleConfig.getString("Messages.Upgrade_command_not_item");
    }
}
