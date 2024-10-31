package com.yxmax.betterbundle.Util;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.yxmax.betterbundle.InvHolder.BundleHolder;
import com.yxmax.betterbundle.InvHolder.CheckBundleHolder;
import com.yxmax.betterbundle.PlaceHolder.PlaceholderExpansion;
import com.yxmax.betterbundle.SQL.DataBases;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.SQL.DataBases.con;
import static com.yxmax.betterbundle.Util.LocaleUtil.*;
import static com.yxmax.betterbundle.Util.MessageUtil.*;

public class PluginUtil {

    public static float BundleVolume;

    public static float BundlePitch;

    public static String BundleSound;

    public static float UpgradeGUIVolume;

    public static float UpgradeGUIPitch;

    public static String UpgradeGUISound;

    public static String inventoryToBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            String pre_encode = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            String encode = compress(pre_encode);
            sendDebug("Bundle inventory encoded string length: " + encode.length());
            return encode;
            //Converts the inventory and its contents to base64, This also saves item meta-data and inventory type
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert inventory to base64.", e);
        }
    }

    public static Inventory inventoryFromBase64(String data, int id) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decoded);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            LZ4BlockInputStream lz4Input = new LZ4BlockInputStream(inputStream, factory.fastDecompressor());
            byte[] buffer = new byte[1024];
            int count;
            while ((count = lz4Input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            ByteArrayInputStream inventoryInput = new ByteArrayInputStream(outputStream.toByteArray());
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inventoryInput);
            Inventory inventory = Bukkit.getServer().createInventory(new BundleHolder(), dataInput.readInt(), color(bundletitle(id)));
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++){
                ItemStack item = null;
                try {
                    item = (ItemStack)dataInput.readObject();
                } catch (IllegalArgumentException|IOException e) {
                    Bukkit.getConsoleSender().sendMessage(color(prefix() + " Convert an item failed! Set type Material.AIR to fix it! (may some module items disappeared! check your server module loader!)"));
                }
                if (item != null) {
                    if (item.getType() != Material.AIR){
                        inventory.setItem(i, item);
                    }
                }
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to decode the class type.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert Inventory to Base64.", e);
        }
    }

    public static Inventory inventoryFromBase64_Check(String data, int id) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decoded);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            LZ4BlockInputStream lz4Input = new LZ4BlockInputStream(inputStream, factory.fastDecompressor());
            byte[] buffer = new byte[1024];
            int count;
            while ((count = lz4Input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            ByteArrayInputStream inventoryInput = new ByteArrayInputStream(outputStream.toByteArray());
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inventoryInput);
            Inventory inventory = Bukkit.getServer().createInventory(new CheckBundleHolder(), dataInput.readInt(), color(bundletitle(id)));
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++){
                ItemStack item = null;
                try {
                    item = (ItemStack)dataInput.readObject();
                } catch (IllegalArgumentException|IOException e) {
                    Bukkit.getConsoleSender().sendMessage(color(prefix() + " Convert an item failed! Set type Material.AIR to fix it! (may some module items disappeared! check your server module loader!)"));
                }
                if (item != null) {
                    if (item.getType() != Material.AIR){
                        inventory.setItem(i, item);
                    }
                }
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to decode the class type.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert Inventory to Base64.", e);
        }
    }

    public static String compress(String str) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(str);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(out,2048,compressor);
        compressedOutput.write(decoded);
        compressedOutput.close();
        byte[] bytes = out.toByteArray();
        String encode = Base64.getEncoder().encodeToString(bytes);
        return encode;
    }

    public static Boolean SlotUp_Mode_NormalJudge() {
        String v = plugin.getConfig().getString("Slot_upgrade.Settings.Mode");
        if (v.equalsIgnoreCase("normal")) {
            return true;
        } else if (v.equalsIgnoreCase("static")) {
            return false;
        } else {
            return true;
        }
    }

    public static int SlotUp_Value() {
        int v = plugin.getConfig().getInt("Slot_upgrade.Settings.Value");
        return v;
    }


    public static int JudgeInvSize(int static_slot, int current_level) {
        int current_slot = static_slot + current_level;
        if (current_slot >= 10 && current_slot <= 18) {
            return 18;
        }
        if (current_slot >= 19 && current_slot <= 27) {
            return 27;
        }
        if (current_slot >= 28 && current_slot <= 36) {
            return 36;
        }
        if (current_slot >= 37 && current_slot <= 45) {
            return 45;
        }
        if (current_slot >= 46 && current_slot <= 54) {
            return 54;
        }
        return static_slot;
    }

    public static ItemStack BarrierDisplay() {
        ItemStack barrier = new ItemStack(Material.BARRIER, 1);
        ItemMeta barrier_meta = barrier.getItemMeta();
        barrier_meta.setDisplayName(color(Bundle_Barrier_Name()));
        barrier.setItemMeta(barrier_meta);
        return barrier;
    }

    public static String prefix() {
        return plugin.getConfig().getString("Prefix");
    }

    public static String locale() {
        return plugin.getConfig().getString("Language");
    }

    public static boolean openUpgrade_MsgBool(){
        return plugin.getConfig().getBoolean("GUI.Upgrade_menu.Open_message");
    }

    public static Boolean bundlein() {
        return plugin.getConfig().getBoolean("Bundle.AllowBundleIn");
    }

    public static Boolean bundleinhelmet() {
        return plugin.getConfig().getBoolean("Bundle.AllowBundlePutInHelmet");
    }

    public static Boolean openBundle_MsgBool() {
        return plugin.getConfig().getBoolean("Bundle.Open_message");
    }

    public static String color(String message) {
        if(!Below116 || !isBelow113 || !is113) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');

                char[] ch = replaceSharp.toCharArray();
                StringBuilder builder = new StringBuilder("");
                for (char c : ch) {
                    builder.append("&" + c);
                }

                message = message.replace(hexCode, builder.toString());
                matcher = pattern.matcher(message);
            }
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Boolean hasOpenPerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.bundle.open");
    }

    public static Boolean hasReloadPerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.command.reload");
    }

    public static Boolean hasGivePerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.command.give");
    }

    public static Boolean hasCheckPerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.command.check");
    }

    public static Boolean hasGetPerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.command.get");
    }

    public static Boolean hasInfoPerm(Player player) {
        if(player.isOp()){
            return true;
        }
        return player.hasPermission("betterbundle.command.info");
    }

    public static Boolean isOp(Player player) {
        return player.isOp();
    }

    public static void ConsoleMsg(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + msg);
    }

    public static void PluginStartUp() {
        Bukkit.getConsoleSender().sendMessage(color("&6-----------------------------------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(color("&e  ______   ______   __  __   __   __   _____    __       ______    "));
        Bukkit.getConsoleSender().sendMessage(color("&e /\\  == \\ /\\  == \\ /\\ \\/\\ \\ /\\ \"-.\\ \\ /\\  __-. /\\ \\     /\\  ___\\   "));
        Bukkit.getConsoleSender().sendMessage(color("&e \\ \\  __< \\ \\  __< \\ \\ \\_\\ \\\\ \\ \\-.  \\\\ \\ \\/\\ \\\\ \\ \\____\\ \\  __\\   "));
        Bukkit.getConsoleSender().sendMessage(color("&e  \\ \\_____\\\\ \\_____\\\\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\____- \\ \\_____\\\\ \\_____\\ "));
        Bukkit.getConsoleSender().sendMessage(color("&e   \\/_____/ \\/_____/ \\/_____/ \\/_/ \\/_/ \\/____/  \\/_____/ \\/_____/ "));
        Bukkit.getConsoleSender().sendMessage(color(""));
        if (locale().equals("zh-CN")) {
            String papi = "&c禁用";
            if(hasPAPI){
                papi = "&a启用";
            }
            Bukkit.getConsoleSender().sendMessage(color("&f 您正在使用: &aCLASSIC &f免费版本"));
            Bukkit.getConsoleSender().sendMessage(color("&f 成功载入 Lz4 数据压缩算法"));
            Bukkit.getConsoleSender().sendMessage(color("&f 插件版本: &av1.4.36"));
            Bukkit.getConsoleSender().sendMessage(color("&f PlaceHolderAPI 支持: " + papi));
            Bukkit.getConsoleSender().sendMessage(color("&f 插件运行于: " + Bukkit.getServer().getVersion()));
        } else {
            String papi = "&cDisabled";
            if(hasPAPI){
                papi = "&aEnabled";
            }
            Bukkit.getConsoleSender().sendMessage(color("&f Plugin is &aCLASSIC &ffree version"));
            Bukkit.getConsoleSender().sendMessage(color("&f Init Lz4 compress method success"));
            Bukkit.getConsoleSender().sendMessage(color("&f Plugin version: &av1.4.36"));
            Bukkit.getConsoleSender().sendMessage(color("&f PlaceHolderAPI Support: " + papi));
            Bukkit.getConsoleSender().sendMessage(color("&f Running on: " + Bukkit.getServer().getVersion()));
        }
        Bukkit.getConsoleSender().sendMessage(color("&6-----------------------------------------------------------------------"));
    }

    public static void ConsoleWarnMsg(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.RED + msg);
    }

    public static int getValueFromLore(ItemStack item, String value) {

        Integer returnVal = 0;
        ItemMeta meta = item.getItemMeta();
        try {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).contains(value)) {
                        String vals = lore.get(i).split(":")[1];
                        vals = ChatColor.stripColor(vals);
                        returnVal = Integer.parseInt(vals.trim());
                    }
                }
            }
        } catch (Exception e) {
            return returnVal;
        }
        return returnVal;
    }

    public final static boolean isNum(String s) {
        if (s != null) {
            s.equals(s.trim());
            return s.matches("^[0-9]*$");
        } else {
            return false;
        }
    }

    public static HashMap<String,File> LocaleMap;

    public static void getFile_writeMap() {
        String path = plugin.getDataFolder().getAbsolutePath() + "\\locale";
        File file = new File(path);
        HashMap<String,File> temp_map = new HashMap<>();
        File[] array = file.listFiles();

        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) {
                temp_map.put(array[i].getName(),array[i].getAbsoluteFile());
            }
        }
        LocaleMap = temp_map;
    }


    public static void reloadSplitSoundValue_Bundle(){
        String value = plugin.getConfig().getString("Bundle.Sound.Value");
        String[] new_value = value.split(":");
        BundleSound = new_value[0];
        BundleVolume = Float.parseFloat(new_value[1]);
        BundlePitch = Float.parseFloat(new_value[2]);
    }

    public static void reloadSplitSoundValue_UpgradeGUI(){
        String value = plugin.getConfig().getString("GUI.Upgrade_menu.Sound.Value");
        String[] new_value = value.split(":");
        UpgradeGUISound = new_value[0];
        UpgradeGUIVolume = Float.parseFloat(new_value[1]);
        UpgradeGUIPitch = Float.parseFloat(new_value[2]);
    }

    public static void reloadSoundValue(){
        reloadSplitSoundValue_Bundle();
        reloadSplitSoundValue_UpgradeGUI();
    }

    public static void DetectServerVersion(){
        String version = Bukkit.getServer().getClass().getPackage().getName();
        if(version.equals("org.bukkit.craftbukkit")){
            String v = Bukkit.getVersion();
            if(v.contains("1.11") || v.contains("1.10") || v.contains("1.9") || v.contains("1.8") || v.contains("1.7") || v.contains("1.6")){
                isBelow113 = true;
                isBelow1122 = true;
            } else if(v.contains("1.12")){
                isBelow113 = true;
            } else if(v.contains("1.13")){
                is113 = true;
            } else if(v.contains("1.14") || v.contains("1.15")){
                Below116 = true;
            } else if(v.contains("1.21")){
                is121 = true;
            }
        }
        if(version.contains("v1_11") || version.contains("v1_10") || version.contains("v1_9") || version.contains("v1_8") || version.contains("v1_7") || version.contains("v1_6")){
            isBelow113 = true;
            isBelow1122 = true;
        } else if(version.contains("v1_12")){
            isBelow113 = true;
        } else if(version.contains("v1_13")){
            is113 = true;
        } else if(version.contains("v1_14") || version.contains("v1_15")){
            Below116 = true;
        } else if(version.contains("v1_21")){
            is121 = true;
        }
    }

    public static void setupPluginPrefix(){
        PluginPrefix = plugin.getConfig().getString("Prefix");
    }

    public static void initPlaceHolderAPI(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansion(plugin).register();
            hasPAPI = true;
        }
    }

    public static void sendMsg(CommandSender commandSender, String msg){
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            player.sendMessage(color(PluginPrefix + msg));
        } else {
            Bukkit.getConsoleSender().sendMessage(color(msg));
        }
    }

    public static void initRandomList(){
        Empty_Random_List.add("fishing:false:false:false");
        Empty_Random_List.add("mining:false:false:false");
        Empty_Random_List.add("harvest:false:false:false");
        Empty_Random_List.add("mobdrop:false:false:false");
        Empty_Random_List.add("deathkeep:false:false:false");
        Empty_Random_List.add("regionpickup:false:false:false");
    }

    public static void sendDebug(String msg){
        if(DEBUG){
            Bukkit.getConsoleSender().sendMessage("[BetterBundle/DEBUG-INFO] " + msg);
        }
    }

    public static void initRandomAbilitiesList(int id){
        if(isFolia){
            FoliaLib.scheduling().asyncScheduler().run(new Runnable() {
                @Override
                public void run() {
                    initListThread(id);
                }
            });
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                initListThread(id);
            }
        });
    }

    public static void initListThread(int id){
        LinkedList<String> ability_set = Empty_Random_List;
        sendDebug("Sending ability list to databases: " + ability_set);
        Gson gson = new Gson();
        String string_set = gson.toJson(ability_set);
        try {
            DataBases.updateAbility(con,string_set,id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFixedBundle(int id,Player player){
        Integer default_slot;
        int bundle_level;
        int max_slot;
        try {
            default_slot = DataBases.GetSlot(con,id);
            if(default_slot == null){
                player.sendMessage(color(PluginPrefix + bundleNotExist()));
                return;
            }
            bundle_level = DataBases.GetLevel(con,id);
            max_slot = DataBases.GetMaxSlot(con,id);
            detectBundle(id,player,default_slot,bundle_level,max_slot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void detectBundle(int id,Player player,int default_slot,int bundle_level,int max_slot) throws Exception {
        if(BundleOccupy.contains(id)){
            player.sendMessage(color(PluginPrefix + bundleOccupy()));
            return;
        }
        openingBundleTemp.put(player.getUniqueId(),id);
        BundleOccupy.add(id);
        if(bundle_level == 0){
            player.openInventory(inventoryFromBase64(DataBases.InvCode(con,id),id));
            if(getSound_Bool("bundle")){
                player.playSound(player.getLocation(), Sound.valueOf(BundleSound),BundleVolume,BundlePitch);
            }
            if(openBundle_MsgBool()){
                player.sendMessage(color(PluginPrefix + openBundle_Msg(id)));
            }
            return;
        }
        int new_max_size = JudgeInvSize(default_slot,bundle_level);
        Inventory before_bundle;
        before_bundle = inventoryFromBase64(DataBases.InvCode(con,id),id);
        if(before_bundle.getSize() == new_max_size){
            sendDebug(player.getName() + " open bundle with current max size");
            Inventory fixed_show_bundle = CheckBarrier(before_bundle,default_slot,bundle_level,new_max_size);
            DataBases.updateinv(con,id,inventoryToBase64(fixed_show_bundle),player.getName());
            player.openInventory(fixed_show_bundle);
            if(openBundle_MsgBool()){
                player.sendMessage(color(PluginPrefix + openBundle_Msg(id)));
            }
            if(getSound_Bool("bundle")){
                player.playSound(player.getLocation(), Sound.valueOf(BundleSound),BundleVolume,BundlePitch);
            }
            return;
        }
        if(before_bundle.getSize() == max_slot){
            sendDebug(player.getName() + " open bundle with always max size");
            Inventory fixed_show_bundle = CheckBarrier(before_bundle,default_slot,bundle_level,max_slot);
            DataBases.updateinv(con,id,inventoryToBase64(fixed_show_bundle),player.getName());
            player.openInventory(fixed_show_bundle);
            if(openBundle_MsgBool()){
                player.sendMessage(color(PluginPrefix + openBundle_Msg(id)));
            }
            if(getSound_Bool("bundle")){
                player.playSound(player.getLocation(), Sound.valueOf(BundleSound),BundleVolume,BundlePitch);
            }
            return;
        }
        if(before_bundle.getSize() != new_max_size){
            sendDebug(player.getName() + " open bundle with new max size");
            Inventory new_create_bundle = Bukkit.createInventory(new BundleHolder(), new_max_size, color(bundletitle(id)));
            for(int i=0;i<before_bundle.getSize();i++){
                if(before_bundle.getItem(i) == null){
                    continue;
                }
                new_create_bundle.setItem(i, before_bundle.getItem(i));
            }
            Inventory fixed_show_bundle = CheckBarrier(new_create_bundle,default_slot,bundle_level,new_max_size);
            DataBases.updateinv(con,id,inventoryToBase64(fixed_show_bundle),player.getName());
            player.openInventory(fixed_show_bundle);
            if(openBundle_MsgBool()){
                player.sendMessage(color(PluginPrefix + openBundle_Msg(id)));
            }
            if(getSound_Bool("bundle")){
                player.playSound(player.getLocation(), Sound.valueOf(BundleSound),BundleVolume,BundlePitch);
            }
        }
    }

    public static Inventory CheckBarrier(Inventory bundle, int default_slot, int bundle_level, int max_size) {
        for(int i=0;i < bundle_level; i++){
            if(bundle.getItem(default_slot + i) == null){
                continue;
            }
            if(bundle.getItem(default_slot + i).getType().equals(Material.BARRIER)){
                bundle.setItem(default_slot + i,new ItemStack(Material.AIR));
            }
        }
        for(int i=(default_slot + bundle_level);i<max_size;i++){
            if(bundle.getItem(i) == null){
                bundle.setItem(i,BarrierDisplay());
                continue;
            }
            if(bundle.getItem(i).getType().equals(Material.BARRIER)){ continue; }
            if(bundle.getItem(i).getType().equals(Material.AIR)){
                bundle.setItem(i,BarrierDisplay());
            }
            bundle.setItem(i,BarrierDisplay());
        }
        return bundle;
    }

    public static void reloadPrefix(){
        PluginPrefix = plugin.getConfig().getString("Prefix");
    }

    public static boolean getSound_Bool(String type){
        switch(type){
            case "bundle":
                return plugin.getConfig().getBoolean("Bundle.Sound.Enabled");
            case "upgrade":
                return plugin.getConfig().getBoolean("GUI.Upgrade_menu.Sound.Enabled");
        }
        return plugin.getConfig().getBoolean("Bundle.Sound.Enabled");
    }

    public static void updateCoin(Player player){
        if(isFolia){
            FoliaLib.scheduling().asyncScheduler().run(new Runnable() {
                @Override
                public void run() {
                    try {
                        int point = DataBases.GetCoin(con, String.valueOf(player.getUniqueId()));
                        BundleCoinMap.put(player.getUniqueId(),point);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            try {
                int point = DataBases.GetCoin(con, String.valueOf(player.getUniqueId()));
                BundleCoinMap.put(player.getUniqueId(),point);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void updateCoin(OfflinePlayer player){
        if(isFolia){
            FoliaLib.scheduling().asyncScheduler().run(new Runnable() {
                @Override
                public void run() {
                    try {
                        int point = DataBases.GetCoin(con, String.valueOf(player.getUniqueId()));
                        BundleCoinMap.put(player.getUniqueId(),point);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            try {
                int point = DataBases.GetCoin(con, String.valueOf(player.getUniqueId()));
                BundleCoinMap.put(player.getUniqueId(),point);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean getUpgradeClickBool(){
        return plugin.getConfig().getBoolean("Upgrade_menu.Sound_enabled");
    }

    public static String getUpgradeClickSound(String type){
        return plugin.getConfig().getString("Upgrade_menu." + type);
    }

    public static ItemStack setCustomSkull(ItemStack head, String base64) {
        if(is121){
            if (base64.isEmpty()) return head;

            try {
                SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.randomUUID(),"betterbundle");
                PlayerTextures textures = profile.getTextures();
                textures.setSkin(getUrlFromBase64(base64));
                profile.setTextures(textures);
                skullMeta.setOwnerProfile(profile);
                head.setItemMeta(skullMeta);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return head;

        }
        if(!isBelow113) {

            if (base64.isEmpty()) return head;

            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), "betterbundle");
            profile.getProperties().put("textures", new Property("textures", base64));

            try {
                Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(skullMeta, profile);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                try {
                    Field mtd = skullMeta.getClass().getDeclaredField("profile");
                    mtd.setAccessible(true);
                    mtd.set(skullMeta, profile);
                } catch (IllegalAccessException | NoSuchFieldException ex2) {
                    ex2.printStackTrace();
                }
            }

            head.setItemMeta(skullMeta);
            return head;
        } else if(isBelow113){

            if (base64.isEmpty()) return head;

            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), "betterbundle");
            profile.getProperties().put("textures", new Property("textures", base64));

            try {
                Field mtd = skullMeta.getClass().getDeclaredField("profile");
                mtd.setAccessible(true);
                mtd.set(skullMeta, profile);
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                ex.printStackTrace();
            }
            head.setItemMeta(skullMeta);
            return head;
        }
        return head;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }
}
