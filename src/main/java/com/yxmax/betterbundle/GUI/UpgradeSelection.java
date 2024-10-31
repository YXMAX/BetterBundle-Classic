package com.yxmax.betterbundle.GUI;

import com.yxmax.betterbundle.InvHolder.*;
import com.yxmax.betterbundle.SQL.DataBases;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.yxmax.betterbundle.BetterBundle.isBelow113;
import static com.yxmax.betterbundle.BundleHeads.HeadStacks.PointBundleDisplay;
import static com.yxmax.betterbundle.SQL.DataBases.UpdateLevel;
import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.PluginUtil.*;
import static com.yxmax.betterbundle.Util.LocaleUtil.*;

public class UpgradeSelection {

    public static Inventory UpgradeSelect(Player player,int id,int page,int bundle_level){
        int static_slot;
        int max_slot;
        int total_upgrade_size;
        try {
            static_slot = DataBases.GetSlot(con,id);
            max_slot = DataBases.GetMaxSlot(con,id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        total_upgrade_size = max_slot - static_slot;
        Inventory guiselect = Bukkit.createInventory(new UpgradeSelectHolder(),45,color(slotuptitle(page)));
        guiselect.setItem(4, PointBundleDisplay(player));
        for(int i=0;i<4;i++){
            guiselect.setItem(i,GlassDisplay());
        }
        for(int i=5;i<9;i++){
            guiselect.setItem(i,GlassDisplay());
        }
        for(int i=37;i<44;i++){
            guiselect.setItem(i,GlassDisplay());
        }
        guiselect.setItem(36,PreviousDisplay());
        if(total_upgrade_size == (9 * page)){
            guiselect.setItem(44,GlassDisplay());
        } else {
            guiselect.setItem(44,NextDisplay());
        }
        int Level = bundle_level;
        if(Level <= -1){
            try {
                Level = DataBases.GetLevel(con,id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(Level == 0 && page == 1){
            for(int i=0;i<9;i++){
                if(i==0){
                    guiselect.setItem(i+18,UnlockedDisplay(i+1));
                } else {
                    guiselect.setItem(i+18,LockedDisplay(i+1));
                }
            }
        }
        if(Level <= ((9 * (page-1))- 1) && page != 1){
            for(int i=0;i<9;i++){
                guiselect.setItem(i+18,LockedDisplay(i+1+((page-1)*9)));
            }
        }
        if(Level >= (9 * (page-1)) && Level <= ((9 * page)-1)){
            int Unlock = Level - (9 * (page-1));
            int Lock = ((9 * page)-1) - Level;
            for(int i=0;i<Unlock;i++){
                guiselect.setItem(i+18,UpgradedDisplay(i+1+((page-1)*9)));
            }
            guiselect.setItem(Level+18-((page-1)*9),UnlockedDisplay(Level+1));
            for(int i=0;i<Lock;i++){
                guiselect.setItem(Unlock+19+i,LockedDisplay(Level+2+i));
            }
        }
        if(Level >= (page * 9)){
            for(int i=0;i<9;i++){
                guiselect.setItem(i+18,UpgradedDisplay(i+1+((page-1)*9)));
            }
        }
        return guiselect;
    }

    private static ItemStack LockedDisplay(Integer level){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 14);
        } else {
            glass = new ItemStack(Material.RED_STAINED_GLASS_PANE,1);
        }
        ItemMeta glass_meta = glass.getItemMeta();
        glass_meta.setDisplayName(color(SlotUp_Locked_Name().replaceAll("%upgrade_level%", String.valueOf(level))));
        glass_meta.setLore(SlotUp_Locked_Lore(level));
        glass.setItemMeta(glass_meta);
        return glass;
    }

    private static ItemStack UnlockedDisplay(Integer level){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 4);
        } else {
            glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE,1);
        }
        ItemMeta glass_meta = glass.getItemMeta();
        glass_meta.setDisplayName(color(SlotUp_Unlocked_Name().replaceAll("%upgrade_level%", String.valueOf(level))));
        glass_meta.setLore(SlotUp_Unlocked_Lore(level));
        glass.setItemMeta(glass_meta);
        return glass;
    }

    private static ItemStack UpgradedDisplay(Integer level){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 5);
        } else {
            glass = new ItemStack(Material.LIME_STAINED_GLASS_PANE,1);
        }
        ItemMeta glass_meta = glass.getItemMeta();
        glass_meta.setDisplayName(color(SlotUp_Upgraded_Name().replaceAll("%upgrade_level%", String.valueOf(level))));
        glass_meta.setLore(SlotUp_Upgraded_Lore());
        glass.setItemMeta(glass_meta);
        return glass;
    }

    private static ItemStack GlassDisplay(){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 7);
        } else {
            glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(color("&l"));
        glass.setItemMeta(meta);
        return glass;
    }

    private static ItemStack PreviousDisplay(){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 3);
        } else {
            glass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(color(SlotUp_Previous_Name()));
        glass.setItemMeta(meta);
        return glass;
    }

    private static ItemStack NextDisplay(){
        ItemStack glass;
        if(isBelow113){
            glass = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short) 1);
        } else {
            glass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(color(SlotUp_Next_Name()));
        glass.setItemMeta(meta);
        return glass;
    }
}
