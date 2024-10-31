package com.yxmax.betterbundle.CommandList;

import com.yxmax.betterbundle.GUI.UpgradeSelection;
import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.*;
import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.BundleHeads.HeadStacks.*;
import static com.yxmax.betterbundle.SQL.DataBases.*;
import static com.yxmax.betterbundle.Util.MessageUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.UpgradeGUIPitch;

public class PluginCommand implements CommandExecutor , TabExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        String prefix = prefix();

        if(args.length == 0){
            sendMsg(commandSender, unknown());
            return true;
        }

        if(args[0].equals("upgrade")){
            if(!(commandSender instanceof Player)){
                commandSender.sendMessage(color(prefix + onlyplayer()));
                return true;
            }
            if((commandSender instanceof Player) && !commandSender.hasPermission("betterbundle.command.upgrade")){
                commandSender.sendMessage(color(prefix + noperm()));
                return true;
            }
            Player player = (Player) commandSender;
            if(player.getEquipment().getItemInMainHand().getType().equals(Material.AIR) || player.getItemInHand() == null){
                player.sendMessage(color(prefix + upgrade_not_item()));
                return true;
            }
            if((player.getItemInHand().getType().equals(Material.getMaterial("SKULL_ITEM")) || player.getItemInHand().getType().equals(Material.getMaterial("PLAYER_HEAD")))){
                if(isBelow113){
                    int id = getValueFromLore(player.getItemInHand(),"ID");
                    if(id == 0){
                        player.sendMessage(color(prefix + upgrade_not_item()));
                        return true;
                    }
                    try {
                        if(!DataBases.GetUpgrade(con,id)){
                            player.sendMessage(color(PluginPrefix + upgrade_false()));
                            return true;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    openingBundleManageTemp.put(player.getUniqueId(),id);
                    BundleUpgradePageTemp.put(player.getUniqueId(),1);
                    player.openInventory(UpgradeSelection.UpgradeSelect(player, id,1,-1));
                    if(openUpgrade_MsgBool()){
                        player.sendMessage(color(PluginPrefix + openUpgrade_Msg(id)));
                    }
                    if(getSound_Bool("upgrade")){
                        player.playSound(player.getLocation(), Sound.valueOf(UpgradeGUISound),UpgradeGUIVolume,UpgradeGUIPitch);
                    }
                    return true;
                }
                if(player.getEquipment().getItemInMainHand().getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey, ItemTagType.INTEGER)){
                    int id = player.getEquipment().getItemInMainHand().getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER);
                    if(id == 0) {
                        player.sendMessage(color(prefix + upgrade_not_item()));
                        return true;
                    }
                    try {
                        if(!DataBases.GetUpgrade(con,id)){
                            player.sendMessage(color(PluginPrefix + upgrade_false()));
                            return true;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    openingBundleManageTemp.put(player.getUniqueId(),id);
                    BundleUpgradePageTemp.put(player.getUniqueId(),1);
                    player.openInventory(UpgradeSelection.UpgradeSelect(player, id,1,-1));
                    if(openUpgrade_MsgBool()){
                        player.sendMessage(color(PluginPrefix + openUpgrade_Msg(id)));
                    }
                    if(getSound_Bool("upgrade")){
                        player.playSound(player.getLocation(), Sound.valueOf(UpgradeGUISound),UpgradeGUIVolume,UpgradeGUIPitch);
                    }
                    return true;
                }
            } else {
                player.sendMessage(color(prefix + upgrade_not_item()));
                return true;
            }
        }

        if(args[0].equals("get")) {
            if((commandSender instanceof Player) && !commandSender.hasPermission("betterbundle.command.get")){
                commandSender.sendMessage(color(prefix + noperm()));
                return true;
            }
            int bundle_type = 0;
            String bundle_upgrade;
            int bundle_max_slot = 0;
            String send_player = "self";
            if(args.length == 1 || args[1].isEmpty()){
                sendMsg(commandSender,nomode());
                return true;
            }
            switch(args[1].toLowerCase()){
                case "mini":
                    bundle_type = 1;
                    break;
                case "tiny":
                    bundle_type = 2;
                    break;
                case "medium":
                    bundle_type = 3;
                    break;
                case "large":
                    bundle_type = 4;
                    break;
                case "huge":
                    bundle_type = 5;
                    break;
                case "massive":
                    bundle_type = 6;
                    break;
                default:
                    sendMsg(commandSender, nomode());
                    return true;
            }
            if(args.length == 2 || args[2].isEmpty()){
                sendMsg(commandSender,noupgrade());
                return true;
            }
            switch(args[2].toLowerCase()){
                case "true":
                case "false":
                    bundle_upgrade = args[2];
                    break;
                default:
                    sendMsg(commandSender, noupgrade());
                    return true;
            }
            if(bundle_upgrade.equals("true")){
                if(bundle_type == 6){
                    sendMsg(commandSender, massiveerror());
                    return true;
                }
                if(args.length == 3 || args[3].isEmpty()){
                    sendMsg(commandSender,nomaxslot());
                    return true;
                }
                switch(args[3].toLowerCase()){
                    case "18":
                        bundle_max_slot = 18;
                        break;
                    case "27":
                        bundle_max_slot = 27;
                        break;
                    case "36":
                        bundle_max_slot = 36;
                        break;
                    case "45":
                        bundle_max_slot = 45;
                        break;
                    case "54":
                        bundle_max_slot = 54;
                        break;
                    default:
                        sendMsg(commandSender, nomaxslot());
                        return true;
                }
                if(args.length == 5){
                    if(!args[4].isEmpty()){
                        if(Bukkit.getPlayerExact(args[4]) != null){
                            if(commandSender instanceof Player){
                                if(!commandSender.getName().equals(args[4])){
                                    send_player = args[4];
                                }
                            } else {
                                send_player = args[4];
                            }
                        } else {
                            sendMsg(commandSender, player_error());
                            return true;
                        }
                    }
                }
                Detail_BundleSettings(bundle_type,bundle_upgrade,bundle_max_slot,send_player,commandSender);
                return true;
            } else {
                if(args.length == 4){
                    if(!args[3].isEmpty()){
                        if(commandSender instanceof Player){
                            if(!commandSender.getName().equals(args[3])){
                                send_player = args[3];
                            }
                        } else {
                            send_player = args[3];
                        }
                    }
                }
                Detail_BundleSettings(bundle_type,bundle_upgrade,bundle_max_slot,send_player,commandSender);
                return true;
            }
        }

        if(args.length == 1 && args[0].equals("reload")){
            if(commandSender instanceof Player && (!hasReloadPerm((Player) commandSender) || !isOp((Player) commandSender))){
                commandSender.sendMessage(color(prefix + noperm()));
                return true;
            }
            plugin.reloadConfig();
            reloadPrefix();
            getFile_writeMap();
            plugin.createLocaleConfig();
            reloadLocale_GUI();
            reloadSoundValue();
            Update_Locale_Config();
            Update_Config();
            sendMsg(commandSender,reload());
            return true;
        }

        if(args[0].equals("info")){
            if(args.length == 1 && !(commandSender instanceof Player)){
                ConsoleMsg(color(info_player_empty()));
                return true;
            }
            if(args.length == 2 && !args[1].isEmpty() && !(commandSender instanceof Player)){
                String owner = args[1];
                try {
                    SelectAllInfo(con,owner);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            Player player = (Player) commandSender;
            if(args.length == 1 && (commandSender instanceof Player)){
                player.sendMessage(prefix + info_player_empty());
            }
            if(args.length == 2 && !args[1].isEmpty() && (commandSender instanceof Player)){
                if(hasInfoPerm(player)){
                    String owner = args[1];
                    try {
                        SelectAllInfoOnline(con,owner,player);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    player.sendMessage(color(prefix + noperm()));
                    return true;
                }
            }
        }

        if(args[0].equals("check")){
            if(args.length >= 1 && !(commandSender instanceof Player)){
                ConsoleMsg(color(onlyplayer()));
                return true;
            }
            Player player = (Player) commandSender;
            if(args.length == 1){
                player.sendMessage(color(prefix + check_id_empty()));
            }
            if(args.length == 2 && !args[1].isEmpty()){
                if(hasCheckPerm(player)) {
                    if (isNum(args[1])) {
                        try {
                            CheckInv(con, Integer.valueOf(args[1]), player);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        player.sendMessage(color(prefix + check_not_id()));
                    }
                } else {
                    player.sendMessage(color(prefix + noperm()));
                    return true;
                }
            }

        }

        if(args[0].equals("give") && commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!hasGivePerm(player)){
                player.sendMessage(color(prefix + noperm()));
                return true;
            }
            if(args.length == 1){
                player.sendMessage(color(prefix + Give_error_type()));
                return true;
            }
            if(args.length == 2){
                player.sendMessage(color(prefix + Give_error_player()));
                return true;
            }
            switch(args[1]){
                case "bundle":
                    if(args.length == 3){
                        player.sendMessage(color(prefix + Give_not_id()));
                        return true;
                    }
                    if(args.length == 4 && !args[2].isEmpty()){
                        Player target = Bukkit.getPlayerExact(args[2]);
                        if(!args[3].isEmpty()){
                            if(isNum(args[3])){
                                try {
                                    if(CheckBundleisExist(con, Integer.valueOf(args[3]))){
                                        giveBundleFromCommand(target, Integer.valueOf(args[3]));
                                        player.sendMessage(color(prefix + Give_bundle_success(Integer.valueOf(args[3]),target.getName())));
                                        target.sendMessage(color(prefix + Give_bundle_player_msg(Integer.valueOf(args[3]))));
                                        return true;
                                    } else {
                                        player.sendMessage(color(prefix + Give_id_not_exist()));
                                        return true;
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                player.sendMessage(color(prefix + Give_not_id()));
                                return true;
                            }
                        } else {
                            player.sendMessage(color(prefix + Give_not_id()));
                            return true;
                        }
                    } else {
                        player.sendMessage(color(prefix + Give_error_onlineplayer()));
                        return true;
                    }
                case "coin":
                    if(args.length == 3){
                        player.sendMessage(color(prefix + Give_not_num()));
                        return true;
                    }
                    if(args.length == 4 && !args[2].isEmpty()){
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        if(!args[3].isEmpty()){
                            if(isNum(args[3])){
                                try {
                                    if(target.hasPlayedBefore()){
                                        CoinAlter(con,player, Integer.valueOf(args[3]));
                                        player.sendMessage(color(prefix + Give_coin_success(Integer.valueOf(args[3]),target.getName())));
                                        if(target.isOnline()){
                                            Player p = Bukkit.getPlayerExact(target.getName());
                                            p.sendMessage(color(prefix + Give_coin_player_msg(Integer.valueOf(args[3]))));
                                        }
                                        return true;
                                    } else {
                                        CoinInsert(con, String.valueOf(target.getUniqueId()),0,0);
                                        CoinAlter(con,player, Integer.valueOf(args[3]));
                                        player.sendMessage(color(prefix + Give_coin_success(Integer.valueOf(args[3]),target.getName())));
                                        return true;
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                player.sendMessage(color(prefix + Give_not_num()));
                                return true;
                            }
                        } else {
                            player.sendMessage(color(prefix + Give_not_num()));
                            return true;
                        }
                    } else {
                        player.sendMessage(color(prefix + Give_error_player()));
                        return true;
                    }
                default:
                    player.sendMessage(color(prefix + Give_error_type()));
                    return true;
            }
        }

        if(args[0].equals("give") && !(commandSender instanceof Player)){
            if(args.length == 1){
                ConsoleMsg(color(Give_error_type()));
                return true;
            }
            if(args.length == 2){
                ConsoleMsg(color(Give_error_player()));
                return true;
            }
            switch(args[1]){
                case "bundle":
                    if(args.length == 3){
                        ConsoleMsg(color(Give_not_id()));
                        return true;
                    }
                    if(args.length == 4 && !args[2].isEmpty()){
                        Player target = Bukkit.getPlayerExact(args[2]);
                        if(!args[3].isEmpty()){
                            if(isNum(args[3])){
                                try {
                                    if(CheckBundleisExist(con, Integer.valueOf(args[3]))){
                                        giveBundleFromCommand(target, Integer.valueOf(args[3]));
                                        ConsoleMsg(color(Give_bundle_success(Integer.valueOf(args[3]),target.getName())));
                                        target.sendMessage(color(prefix + Give_bundle_player_msg(Integer.valueOf(args[3]))));
                                        return true;
                                    } else {
                                        ConsoleMsg(color(Give_id_not_exist()));
                                        return true;
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                ConsoleMsg(color(Give_not_id()));
                                return true;
                            }
                        } else {
                            ConsoleMsg(color(Give_not_id()));
                            return true;
                        }
                    } else {
                        ConsoleMsg(color(Give_error_onlineplayer()));
                        return true;
                    }
                case "coin":
                    if(args.length == 3){
                        ConsoleMsg(color(Give_not_num()));
                        return true;
                    }
                    if(args.length == 4 && !args[2].isEmpty()){
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        if(!args[3].isEmpty()){
                            if(isNum(args[3])){
                                try {
                                    if(!hasData(con, String.valueOf(target.getUniqueId()))){
                                        CoinInsert(con, String.valueOf(target.getUniqueId()), 0,0);
                                    }
                                    CoinAlter(con,target, Integer.valueOf(args[3]));
                                    ConsoleMsg(color(Give_coin_success(Integer.valueOf(args[3]),target.getName())));
                                    if(target.isOnline()){
                                        Player p = Bukkit.getPlayerExact(target.getName());
                                        p.sendMessage(color(prefix + Give_coin_player_msg(Integer.valueOf(args[3]))));
                                        updateCoin(p);
                                    }
                                    return true;
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                ConsoleMsg(color(Give_not_num()));
                                return true;
                            }
                        } else {
                            ConsoleMsg(color(Give_not_num()));
                            return true;
                        }
                    } else {
                        ConsoleMsg(color(Give_error_player()));
                        return true;
                    }
                default:
                    ConsoleMsg(color(Give_error_type()));
                    return true;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        LinkedList<String> tips = new LinkedList<>();

        if(args.length == 1){
            List<String> firstArgList = new ArrayList<>();
            firstArgList.add("upgrade");
            List<String> opList = Arrays.asList("upgrade", "get", "info", "check", "reload", "give");
            if(hasGivePerm(player)){
                firstArgList.add("give");
            }
            if(hasCheckPerm(player)){
                firstArgList.add("check");
            }
            if(hasInfoPerm(player)){
                firstArgList.add("info");
            }
            if(hasReloadPerm(player)){
                firstArgList.add("reload");
            }
            if(hasGetPerm(player)){
                firstArgList.add("get");
            }
            if(isOp(player)){
                firstArgList = opList;
            }
            if(args[0].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[0].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 2 && args[0].equals("get") && hasGetPerm(player)){
            List<String> firstArgList = Arrays.asList("massive", "huge", "large", "medium", "tiny", "mini");
            if(args[1].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[1].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 2 && args[0].equals("give") && hasGivePerm(player)){
            List<String> firstArgList = Arrays.asList("bundle", "coin");
            if(args[1].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[1].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 3 && args[0].equals("give") && hasGivePerm(player) && !args[1].isEmpty()){
            if(args[2].isEmpty()){
                Bukkit.getOnlinePlayers().forEach(p -> tips.add(p.getName()));
                return tips;
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                        tips.add(p.getName());
                    }
                });
                return tips;
            }
        }

        if(args.length == 4 && args[0].equals("give") && !args[1].isEmpty() && !args[2].isEmpty() && hasGivePerm(player)){
            List<String> firstArgList = Arrays.asList("[Number]");
            if(args[3].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[3].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 3 && args[0].equals("get") && hasGetPerm(player)){
            List<String> firstArgList = Arrays.asList("true", "false");
            if(args[2].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[2].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 4 && args[0].equals("get") && args[2].equals("true")){
            List<String> firstArgList = Arrays.asList("18", "27", "36", "45", "54");
            if(args[3].isEmpty()){
                tips.addAll(firstArgList);
                return tips;
            } else {
                for(String firstArg : firstArgList) {
                    if (firstArg.toLowerCase().startsWith(args[3].toLowerCase())) {
                        tips.add(firstArg);
                    }
                }
                return tips;
            }
        }

        if(args.length == 5 && args[0].equals("get") && args[2].equals("true") && !args[3].isEmpty()){
            if(args[4].isEmpty()){
                Bukkit.getOnlinePlayers().forEach(p -> tips.add(p.getName()));
                return tips;
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(args[4].toLowerCase())) {
                        tips.add(p.getName());
                    }
                });
                return tips;
            }
        }

        if(args.length == 4 && args[0].equals("get") && args[2].equals("false")){
            if(args[3].isEmpty()){
                Bukkit.getOnlinePlayers().forEach(p -> tips.add(p.getName()));
                return tips;
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.getName().toLowerCase().startsWith(args[3].toLowerCase())) {
                        tips.add(p.getName());
                    }
                });
                return tips;
            }
        }

        return null;
    }
}
