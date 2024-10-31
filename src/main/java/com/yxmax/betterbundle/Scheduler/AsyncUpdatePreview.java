package com.yxmax.betterbundle.Scheduler;

import com.google.gson.Gson;
import com.yxmax.betterbundle.BundlePreview.Backpack;
import com.yxmax.betterbundle.BundlePreview.BackpackContent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.HashMap;
import java.util.UUID;

import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.BetterBundle.plugin;
import static com.yxmax.betterbundle.Util.PluginUtil.inventoryFromBase64;

public class AsyncUpdatePreview {

    public static void updatePreview(Player player,Inventory inventory,int hand,int id){
        if(isFolia){
            FoliaLib.scheduling().entitySpecificScheduler(player).run(new Runnable() {
                @Override
                public void run() {
                    ItemStack bundle;
                    if(hand == 1){
                        bundle = player.getEquipment().getItemInMainHand();
                    } else {
                        bundle = player.getEquipment().getItemInOffHand();
                    }
                    if(!bundle.hasItemMeta()){
                        return;
                    }
                    if(!bundle.getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey,ItemTagType.INTEGER)){
                        return;
                    }
                    if(bundle.getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER) != id){
                        return;
                    }
                    ItemMeta meta_fix = bundle.getItemMeta();
                    Backpack backpack = new Backpack(inventory.getSize(), UUID.randomUUID());
                    HashMap<Integer, ItemStack> map = new HashMap<>();
                    ItemStack[] content = inventory.getContents();
                    for(int i=0;i<inventory.getSize();i++){
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
                }
            },null);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    ItemStack bundle;
                    if(hand == 1){
                        bundle = player.getEquipment().getItemInMainHand();
                    } else {
                        bundle = player.getEquipment().getItemInOffHand();
                    }
                    if(!bundle.hasItemMeta()){
                        return;
                    }
                    if(!bundle.getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey,ItemTagType.INTEGER)){
                        return;
                    }
                    if(bundle.getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER) != id){
                        return;
                    }
                    ItemMeta meta_fix = bundle.getItemMeta();
                    Backpack backpack = new Backpack(inventory.getSize(), UUID.randomUUID());
                    HashMap<Integer, ItemStack> map = new HashMap<>();
                    ItemStack[] content = inventory.getContents();
                    for(int i=0;i<inventory.getSize();i++){
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
                }
            });
        }
    }
}
