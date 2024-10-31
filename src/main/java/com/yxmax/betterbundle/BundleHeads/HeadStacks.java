package com.yxmax.betterbundle.BundleHeads;

import com.google.gson.Gson;
import com.yxmax.betterbundle.BundlePreview.Backpack;
import com.yxmax.betterbundle.BundlePreview.BackpackContent;
import com.yxmax.betterbundle.InvHolder.BundleHolder;
import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.MessageUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;
import static com.yxmax.betterbundle.Util.LocaleUtil.*;


public class HeadStacks {

    private static String Redirect_InvCode(int type){
        switch(type){
            case 1:
                return "rO0ABXcEAAAACXBwcHBwcHBwcA==";
            case 2:
                return "rO0ABXcEAAAAEnBwcHBwcHBwcHBwcHBwcHBwcA==";
            case 3:
                return "rO0ABXcEAAAAG3BwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcA==";
            case 4:
                return "rO0ABXcEAAAAJHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcA==";
            case 5:
                return "rO0ABXcEAAAALXBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcA==";
            case 6:
                return "rO0ABXcEAAAANnBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcA==";
            default:
                return "rO0ABXcEAAAANnBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcHBwcA==";
        }
    }

    public static void Detail_BundleSettings(int type, String upgrade, int max_slot, String player, CommandSender commandSender){
        if((type * 9) >= max_slot && upgrade.equalsIgnoreCase("true")){
            sendMsg(commandSender, maxsloterror());
            return;
        }
        String basic_invcode = Redirect_InvCode(type);
        String invcode = null;
        try {
            invcode = compress(basic_invcode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Boolean upgrade_toggle = Boolean.valueOf(upgrade);
        String head_textures = plugin.getConfig().getString("Bundle.Head-textures");
        ItemStack bundle;
        if(isBelow113){
            bundle = new ItemStack(Material.getMaterial("SKULL_ITEM"),1,(short) 3);
        } else {
            bundle = new ItemStack(Material.PLAYER_HEAD);
        }
        bundle = setCustomSkull(bundle,head_textures);
        int id;
        if(player.equals("self")){
            player = commandSender.getName();
        }
        try {
            id = DataBases.insertBundle(con,(type * 9),0,upgrade_toggle,player,max_slot,invcode,null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ItemMeta meta = bundle.getItemMeta();
        meta.setDisplayName(color(bundlename()));
        meta.setLore(bundlelore(id));
        bundle.setItemMeta(meta);
        if(isBelow113){
            Player target = Bukkit.getPlayerExact(player);
            sendMsg(commandSender, getBundleSuccess_Third(target,id));
            target.sendMessage(color(PluginPrefix + getBundleSuccess_Player(id)));
            if(target.getInventory().firstEmpty() == -1){
                target.getWorld().dropItem(target.getLocation(),bundle);
                initRandomAbilitiesList(id);
                return;
            }
            target.getInventory().addItem(bundle);
            initRandomAbilitiesList(id);
            sendDebug(player + " try to get a bundle: " + id);
            return;
        }
        ItemMeta meta_fix = bundle.getItemMeta();
        meta_fix.getCustomTagContainer().setCustomTag(BundleKey, ItemTagType.INTEGER, id);

        //BundlePreview Support
        Backpack backpack = new Backpack(type*9,UUID.randomUUID());
        Inventory preview_inventory = inventoryFromBase64(invcode,id);
        HashMap<Integer,ItemStack> map = new HashMap<>();
        ItemStack[] content = preview_inventory.getContents();
        for(int i=0;i<preview_inventory.getSize();i++){
            if(content[i] == null || content[i].getType().equals(Material.AIR)){
                continue;
            }
            map.put(i,content[i]);
        }
        backpack.setItems(map);
        BackpackContent backpackContent = new BackpackContent(backpack);
        NamespacedKey preview = new NamespacedKey(plugin,"BetterBundlePreview");
        meta_fix.getCustomTagContainer().setCustomTag(preview, ItemTagType.STRING, new Gson().toJson(backpackContent));


        bundle.setItemMeta(meta_fix);
        Player target = Bukkit.getPlayerExact(player);
        sendMsg(commandSender, getBundleSuccess_Third(target,id));
        target.sendMessage(color(PluginPrefix + getBundleSuccess_Player(id)));
        if(target.getInventory().firstEmpty() == -1){
            target.getWorld().dropItem(target.getLocation(),bundle);
            initRandomAbilitiesList(id);
            return;
        }
        target.getInventory().addItem(bundle);
        initRandomAbilitiesList(id);
        sendDebug(player + " try to get a bundle: " + id);
    }

    public static void giveBundleFromCommand(Player target, Integer id){
        String head_textures = plugin.getConfig().getString("Bundle.Head-textures");
        ItemStack bundle;
        if(isBelow113){
            bundle = new ItemStack(Material.getMaterial("SKULL_ITEM"),1,(short) 3);
        } else {
            bundle = new ItemStack(Material.PLAYER_HEAD);
        }
        bundle = setCustomSkull(bundle,head_textures);
        ItemMeta meta = bundle.getItemMeta();
        meta.setDisplayName(color(bundlename()));
        meta.setLore(bundlelore(id));
        bundle.setItemMeta(meta);
        if(isBelow113){
            target.sendMessage(color(PluginPrefix + getBundleSuccess_Player(id)));
            if(target.getInventory().firstEmpty() == -1){
                target.getWorld().dropItem(target.getLocation(),bundle);
                return;
            }
            target.getInventory().addItem(bundle);
            return;
        }
        ItemMeta meta_fix = bundle.getItemMeta();
        meta_fix.getCustomTagContainer().setCustomTag(BundleKey, ItemTagType.INTEGER, id);
        bundle.setItemMeta(meta_fix);
        if(target.getInventory().firstEmpty() == -1){
            target.getWorld().dropItem(target.getLocation(),bundle);
            return;
        }
        target.getInventory().addItem(bundle);
    }

    public static ItemStack PointBundleDisplay(Player player){
        String head_textures = plugin.getConfig().getString("Bundle.Head-textures");
        ItemStack bundle;
        if(isBelow113){
            bundle = new ItemStack(Material.getMaterial("SKULL_ITEM"),1,(short) 3);
        } else {
            bundle = new ItemStack(Material.PLAYER_HEAD);
        }
        bundle = setCustomSkull(bundle,head_textures);
        ItemMeta meta = bundle.getItemMeta();
        meta.setDisplayName(color(slotupbundlename(player)));
        meta.setLore(slotupbundlelore());
        bundle.setItemMeta(meta);
        return bundle;
    }
}
