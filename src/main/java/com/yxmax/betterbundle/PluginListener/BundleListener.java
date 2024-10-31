package com.yxmax.betterbundle.PluginListener;

import com.yxmax.betterbundle.GUI.UpgradeSelection;
import com.yxmax.betterbundle.InvHolder.CheckBundleHolder;
import com.yxmax.betterbundle.InvHolder.UpgradeSelectHolder;
import com.yxmax.betterbundle.SQL.DataBases;
import com.yxmax.betterbundle.InvHolder.BundleHolder;
import com.yxmax.betterbundle.Scheduler.UpdateUpgradeScheduler;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.BetterBundle.openingBundleManageTemp;
import static com.yxmax.betterbundle.GUI.UpgradeSelection.UpgradeSelect;
import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Scheduler.AsyncUpdatePreview.updatePreview;
import static com.yxmax.betterbundle.Util.CheckVarible.PlayerCoinOutPut;
import static com.yxmax.betterbundle.Util.LocaleUtil.*;
import static com.yxmax.betterbundle.Util.MessageUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class BundleListener implements Listener {

    @EventHandler
    public void onBundleClickEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)
                || player.getEquipment().getItemInMainHand() == null){
            return;
        }
        if( (event.getHand() == EquipmentSlot.HAND && (player.getEquipment().getItemInMainHand().getType().equals(Material.getMaterial("SKULL_ITEM")) || player.getEquipment().getItemInMainHand().getType().equals(Material.getMaterial("PLAYER_HEAD")) ))){
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if(!hasOpenPerm(player)){
                    event.setCancelled(true);
                    player.sendMessage(color(PluginPrefix + noperm()));
                    return;
                }
                if(isBelow113){
                    int id = getValueFromLore(player.getItemInHand(),"ID");
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " try to open bundle though Lore detect: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                    return;
                }
                ItemStack hand = player.getEquipment().getItemInMainHand();
                if(hand.getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey, ItemTagType.INTEGER)){
                    int id = hand.getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER);
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " try to open bundle though CustomTag: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                } else {
                    int id = getValueFromLore(player.getEquipment().getItemInMainHand(),"ID");
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " try to open bundle though Lore detect: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                }
            }
        }
    }

    @EventHandler
    public void onBundleOffHandPlaceEvent(BlockPlaceEvent event){
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            Player player = event.getPlayer();
            if(player.getEquipment().getItemInOffHand() == null || player.getEquipment().getItemInOffHand().getType().equals(Material.AIR)){
                return;
            }
            if(player.getEquipment().getItemInOffHand().getType().equals(Material.getMaterial("SKULL_ITEM")) || player.getEquipment().getItemInOffHand().getType().equals(Material.PLAYER_HEAD)){
                if(!hasOpenPerm(player) || !isOp(player)){
                    player.sendMessage(color(PluginPrefix + noperm()));
                    return;
                }
                if(isBelow113){
                    int id = getValueFromLore(player.getEquipment().getItemInOffHand(),"ID");
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " off hand try to open bundle though Lore detect: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                    return;
                }
                ItemStack hand = player.getEquipment().getItemInOffHand();
                if(hand.getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey, ItemTagType.INTEGER)){
                    int id = hand.getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER);
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " off hand try to open bundle though CustomTag: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                } else {
                    int id = getValueFromLore(player.getEquipment().getItemInOffHand(),"ID");
                    if(id == 0){
                        return;
                    }
                    sendDebug(player + " off hand try to open bundle though Lore detect: " + id);
                    event.setCancelled(true);
                    openFixedBundle(id,player);
                }
            }
        }
    }

    @EventHandler
    public void onOpenBundleUpgradeEvent(PlayerSwapHandItemsEvent event){
        if(!plugin.getConfig().getBoolean("GUI.Upgrade_menu.Keys")){
            return;
        }
        Player player = event.getPlayer();
        if(player.getEquipment().getItemInMainHand().getType().equals(Material.AIR) || player.getItemInHand() == null){
            return;
        }
        if(player.isSneaking() && (player.getItemInHand().getType().equals(Material.getMaterial("SKULL_ITEM")) || player.getItemInHand().getType().equals(Material.getMaterial("PLAYER_HEAD")))){
            if(isBelow113){
                int id = getValueFromLore(player.getItemInHand(),"ID");
                if(id == 0){
                    return;
                }
                try {
                    if(!DataBases.GetUpgrade(con,id)){
                        player.sendMessage(color(PluginPrefix + upgrade_false()));
                        return;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                event.setCancelled(true);
                openingBundleManageTemp.put(player.getUniqueId(),id);
                BundleUpgradePageTemp.put(player.getUniqueId(),1);
                player.openInventory(UpgradeSelection.UpgradeSelect(player, id,1,-1));
                if(openUpgrade_MsgBool()){
                    player.sendMessage(color(PluginPrefix + openUpgrade_Msg(id)));
                }
                if(getSound_Bool("upgrade")){
                    player.playSound(player.getLocation(), Sound.valueOf(UpgradeGUISound),UpgradeGUIVolume,UpgradeGUIPitch);
                }
                return;
            }
            if(player.getEquipment().getItemInMainHand().getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey,ItemTagType.INTEGER)){
                int id = player.getEquipment().getItemInMainHand().getItemMeta().getCustomTagContainer().getCustomTag(BundleKey,ItemTagType.INTEGER);
                if(id == 0){
                    return;
                }
                try {
                    if(!DataBases.GetUpgrade(con,id)){
                        player.sendMessage(color(PluginPrefix + upgrade_false()));
                        return;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                event.setCancelled(true);
                openingBundleManageTemp.put(player.getUniqueId(),id);
                BundleUpgradePageTemp.put(player.getUniqueId(),1);
                player.openInventory(UpgradeSelection.UpgradeSelect(player, id,1,-1));
                if(openUpgrade_MsgBool()){
                    player.sendMessage(color(PluginPrefix + openUpgrade_Msg(id)));
                }
                if(getSound_Bool("upgrade")){
                    player.playSound(player.getLocation(), Sound.valueOf(UpgradeGUISound),UpgradeGUIVolume,UpgradeGUIPitch);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onBundleCloseEvent(InventoryCloseEvent event) throws Exception {
        if(event.getInventory().getHolder() instanceof BundleHolder){
            HumanEntity entity = event.getPlayer();
            int id = openingBundleTemp.get(entity.getUniqueId());
            openingBundleTemp.remove(entity.getUniqueId());
            BundleOccupy.remove(id);
            String invcode = inventoryToBase64(event.getInventory());
            DataBases.updateinv(con,id,invcode,entity.getName());

            //BundlePreview test
            if(!isBelow113){
                Player player = (Player) entity;
                if(player.getEquipment().getItemInMainHand() == null || player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)){
                    updatePreview(player,event.getInventory(),0,id);
                    return;
                }
                if(player.getEquipment().getItemInOffHand() == null || player.getEquipment().getItemInOffHand().getType().equals(Material.AIR)){
                    updatePreview(player,event.getInventory(),1,id);
                    return;
                }
                if(player.getEquipment().getItemInOffHand().hasItemMeta()){
                    if(player.getEquipment().getItemInOffHand().getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey,ItemTagType.INTEGER)){
                        updatePreview(player,event.getInventory(),0,id);
                    }
                }
                if(player.getEquipment().getItemInMainHand().hasItemMeta()){
                    if(player.getEquipment().getItemInMainHand().getItemMeta().getCustomTagContainer().hasCustomTag(BundleKey,ItemTagType.INTEGER)){
                        updatePreview(player,event.getInventory(),1,id);
                    }
                }
            }
        }
        if(event.getInventory().getHolder() instanceof CheckBundleHolder){
            HumanEntity entity = event.getPlayer();
            String out = openCheckingBundleTemp.get(entity.getUniqueId());
            openCheckingBundleTemp.remove(entity.getUniqueId());
            String[] out_detail = out.split(":");
            String invcode = inventoryToBase64(event.getInventory());
            DataBases.updateinv(con, Integer.valueOf(out_detail[1]),invcode,out_detail[0]);
        }
    }

    @EventHandler
    public void onPackagePutInBundleEvent(InventoryClickEvent event){
        String prefix = prefix();
        if(event.getInventory().getHolder() instanceof BundleHolder){
            if(event.getRawSlot() > event.getInventory().getSize()){
                Player player = (Player) event.getWhoClicked();
                ItemStack clickeditem = event.getCurrentItem();
                if(clickeditem == null){
                    return;
                }
                if(clickeditem.getItemMeta() == null){
                    return;
                }
                if(!clickeditem.getItemMeta().hasDisplayName()){
                    return;
                }
                if(player.getInventory().getItemInMainHand().equals(clickeditem)){
                    event.setCancelled(true);
                    return;
                }
                if(clickeditem.getItemMeta().getDisplayName().equals(color(bundlename())) && !bundlein() && !player.getInventory().getItemInMainHand().equals(clickeditem)){
                    event.setCancelled(true);
                    player.sendMessage(color(prefix + noput()));
                }
            }
        }
    }

    @EventHandler
    public void onBundleClickBarrierEvent(InventoryClickEvent event){
        if(event.getInventory().getHolder() instanceof BundleHolder){
            if(event.getRawSlot() < event.getInventory().getSize()){
                if(event.getCurrentItem() == null){
                    return;
                }
                if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBundleInHelmetEvent(InventoryCloseEvent event){
        String prefix = prefix();
        Player player = (Player) event.getPlayer();
        if(!bundleinhelmet()){
            if(player.getInventory().getHelmet() == null){
                return;
            }
            if(player.getInventory().getHelmet().getItemMeta().getDisplayName().equals(color(bundlename()))){
                ItemStack bundle = player.getInventory().getHelmet();
                player.getInventory().setHelmet(new ItemStack(Material.AIR,1));
                player.getInventory().addItem(bundle);
                player.sendMessage(color(prefix + nohelmet()));
            }
        }
    }

    @EventHandler
    public void onUpgradeSelectionClickEvent(InventoryClickEvent event) {
        Boolean Slotup_judge = SlotUp_Mode_NormalJudge();
        int Slotup_value = SlotUp_Value();
        String prefix = prefix();
        if (event.getInventory().getHolder() instanceof UpgradeSelectHolder) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            if (event.getCurrentItem() == null) {
                return;
            }
            if (isBelow113) {
                int page = BundleUpgradePageTemp.get(player.getUniqueId());
                int bundle_id = openingBundleManageTemp.get(player.getUniqueId());
                if (slot >= 18 && slot <= 26) {
                    ItemStack item = event.getCurrentItem();
                    event.setCancelled(true);
                    if (item.getType().equals(Material.getMaterial("STAINED_GLASS_PANE")) && item.getDurability() == 4) {
                        event.setCancelled(true);
                        int menu_level = (slot - 17) + ((page - 1) * 9);
                        int require_coin;
                        int coin = PlayerCoinOutPut(player);
                        if (Slotup_judge) {
                            require_coin = menu_level + Slotup_value - 1;
                        } else {
                            require_coin = Slotup_value;
                        }
                        if (coin >= require_coin) {
                            int less_coin = coin - require_coin;
                            try {
                                player.openInventory(UpgradeSelect(player, bundle_id, page,menu_level));
                                player.sendMessage(color(prefix + upgrade_success(menu_level,bundle_id)));
                                if(getUpgradeClickBool()){
                                    String[] sound_detail = getUpgradeClickSound("Success_value").split(":");
                                    player.playSound(player.getLocation(),Sound.valueOf(sound_detail[0]),Float.parseFloat(sound_detail[1]),Float.parseFloat(sound_detail[2]));
                                }
                                Thread thread = new Thread(new UpdateUpgradeScheduler(player,bundle_id,less_coin,menu_level));
                                thread.start();
                                return;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (coin < require_coin) {
                            player.sendMessage(color(prefix + upgrade_fail(bundle_id)));
                            if(getUpgradeClickBool()){
                                String[] sound_detail = getUpgradeClickSound("Failed_value").split(":");
                                player.playSound(player.getLocation(),Sound.valueOf(sound_detail[0]),Float.parseFloat(sound_detail[1]),Float.parseFloat(sound_detail[2]));
                            }
                            return;
                        }
                    }
                    return;
                }
                if (event.getCurrentItem().getType().equals(Material.getMaterial("STAINED_GLASS_PANE")) && event.getCurrentItem().getDurability() == 1) {
                    BundleUpgradePageTemp.put(player.getUniqueId(), page + 1);
                    player.openInventory(UpgradeSelect(player, bundle_id, page + 1,-1));
                    return;
                }
                if (event.getCurrentItem().getType().equals(Material.getMaterial("STAINED_GLASS_PANE")) && event.getCurrentItem().getDurability() == 3) {
                    if (page == 1) {
                        player.closeInventory();
                        return;
                    } else {
                        BundleUpgradePageTemp.put(player.getUniqueId(), page - 1);
                        player.openInventory(UpgradeSelect(player, bundle_id, page - 1,-1));
                        return;
                    }
                }
                event.setCancelled(true);
                return;
            } else {
                int page = BundleUpgradePageTemp.get(player.getUniqueId());
                int bundle_id = openingBundleManageTemp.get(player.getUniqueId());
                if (slot >= 18 && slot <= 26) {
                    ItemStack item = event.getCurrentItem();
                    event.setCancelled(true);
                    if (item.getType().equals(Material.YELLOW_STAINED_GLASS_PANE)) {
                        event.setCancelled(true);
                        int menu_level = (slot - 17) + ((page - 1) * 9);
                        int require_coin;
                        int coin = PlayerCoinOutPut(player);
                        if (Slotup_judge) {
                            require_coin = menu_level + Slotup_value - 1;
                        } else {
                            require_coin = Slotup_value;
                        }
                        if (coin >= require_coin) {
                            int less_coin = coin - require_coin;
                            try {
                                player.openInventory(UpgradeSelect(player, bundle_id, page, menu_level));
                                player.sendMessage(color(prefix + upgrade_success(menu_level,bundle_id)));
                                if(getUpgradeClickBool()){
                                    String[] sound_detail = getUpgradeClickSound("Success_value").split(":");
                                    player.playSound(player.getLocation(),Sound.valueOf(sound_detail[0]),Float.parseFloat(sound_detail[1]),Float.parseFloat(sound_detail[2]));
                                }
                                Thread thread = new Thread(new UpdateUpgradeScheduler(player,bundle_id,less_coin,menu_level));
                                thread.start();
                                return;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (coin < require_coin) {
                            player.sendMessage(color(prefix + upgrade_fail(bundle_id)));
                            if(getUpgradeClickBool()){
                                String[] sound_detail = getUpgradeClickSound("Failed_value").split(":");
                                player.playSound(player.getLocation(),Sound.valueOf(sound_detail[0]),Float.parseFloat(sound_detail[1]),Float.parseFloat(sound_detail[2]));
                            }
                            return;
                        }
                    }
                    return;
                }
                if (event.getCurrentItem().getType().equals(Material.ORANGE_STAINED_GLASS_PANE)) {
                    BundleUpgradePageTemp.put(player.getUniqueId(), page + 1);
                    player.openInventory(UpgradeSelect(player, bundle_id, page + 1, -1));
                    return;
                }
                if (event.getCurrentItem().getType().equals(Material.LIGHT_BLUE_STAINED_GLASS_PANE)) {
                    if (page == 1) {
                        player.closeInventory();
                        return;
                    } else {
                        BundleUpgradePageTemp.put(player.getUniqueId(), page - 1);
                        player.openInventory(UpgradeSelect(player, bundle_id, page - 1, -1));
                        return;
                    }
                }
                event.setCancelled(true);
                return;
            }
        }
    }
}
