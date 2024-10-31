package com.yxmax.betterbundle;

import com.yxmax.betterbundle.CommandList.PluginCommand;
import com.yxmax.betterbundle.Metric.Metrics;
import com.yxmax.betterbundle.PluginListener.*;
import net.jpountz.lz4.LZ4Factory;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.yxmax.betterbundle.SQL.DataBases.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class BetterBundle extends JavaPlugin {

    public static BetterBundle plugin;

    public static LZ4Factory factory;

    public static Boolean DEBUG = false;

    public static String PluginPrefix;

    private static File LocaleConfigFile;
    public static FileConfiguration LocaleConfig;

    private static File CN_File;

    private static File US_File;

    public static Permission perms = null;

    public static Boolean is121 = false;

    public static Boolean is113 = false;

    public static Boolean isBelow113 = false;

    public static Boolean isBelow1122 = false;

    public static Boolean Below116 = false;

    public static Boolean hasPAPI = false;

    public static LinkedList<String> Empty_Random_List = new LinkedList<>();

    public static MorePaperLib FoliaLib;

    public static Boolean isFolia = false;

    public static NamespacedKey BundleKey;

    public static HashMap<UUID,Integer> openingBundleTemp = new HashMap<>();

    public static HashMap<UUID,Integer> openingBundleManageTemp = new HashMap<>();

    public static HashMap<UUID,String> openCheckingBundleTemp = new HashMap<>();

    public static HashMap<UUID,Integer> BundleUpgradePageTemp = new HashMap<>();

    public static HashMap<UUID,Integer> BundleCoinMap = new HashMap<>();

    public static HashSet<Integer> BundleOccupy = new HashSet<>();

    @Override
    public void onEnable() {
        plugin = this;
        new Metrics(this,23587);
        saveDefaultConfig();
        initLz4Factory();
        initCommand();
        initListeners();
        detectDEBUG();
        setupBundleKey();
        initMorePaperLib();
        DetectFolia();
        initRandomList();
        initPlaceHolderAPI();
        setupPluginPrefix();
        setupPermissions();
        createLocaleConfig_Detect();
        DetectServerVersion();
        getFile_writeMap();
        reloadSoundValue();
        setupDataBaseConnection();
        Update_Locale_Config();
        Update_Config();
        PluginStartUp();

    }

    @Override
    public void onDisable() {
        PluginDisabled();
    }

    protected void initLz4Factory() {
        factory = LZ4Factory.fastestInstance();
    }

    protected void initCommand() {
        Bukkit.getPluginCommand("BetterBundle").setExecutor(new PluginCommand());
    }

    protected void detectDEBUG(){
        DEBUG = plugin.getConfig().getBoolean("Debug-mode");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private static void DetectFolia() {
        isFolia = FoliaLib.scheduling().isUsingFolia();
    }

    private void setupBundleKey(){
        if(!isBelow113){
            BundleKey = new NamespacedKey(this, "BetterBundle");
        }
    }

    public static void Update_Locale_Config(){
        boolean change = false;
        String locale = locale();
        Configuration defaults = null;
        try {
            InputStream resource = plugin.getResource("locale/" + locale + ".yml");
            File temp = File.createTempFile("stream2file", ".tmp");
            FileUtils.copyInputStreamToFile(resource,temp);
            defaults = YamlConfiguration.loadConfiguration(temp);
            temp.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String defaultKey : defaults.getKeys(true)) {
            if (!LocaleConfig.contains(defaultKey)) {
                LocaleConfig.set(defaultKey, defaults.get(defaultKey));
                change = true;
            }
        }
        if (change) {
            try {
                LocaleConfig.save(LocaleConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void Update_Config(){
        boolean change = false;
        Configuration defaults = null;
        File config = new File(plugin.getDataFolder(),"/config.yml");
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            InputStream resource = plugin.getResource("config.yml");
            File temp = File.createTempFile("stream2file", ".tmp");
            FileUtils.copyInputStreamToFile(resource,temp);
            defaults = YamlConfiguration.loadConfiguration(temp);
            temp.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String defaultKey : defaults.getKeys(true)) {
            if (!configuration.contains(defaultKey)) {
                configuration.set(defaultKey, defaults.get(defaultKey));
                change = true;
            }
        }
        if (change) {
            try {
                configuration.save(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initMorePaperLib(){
        FoliaLib = new MorePaperLib(plugin);
    }

    private void createLocaleConfig_Detect() {
        CN_File = new File(getDataFolder() + "/locale/", "zh-CN.yml");
        if (!CN_File.exists()) {
            CN_File.getParentFile().mkdirs();
            saveResource("locale/zh-CN.yml", false);
        }

        US_File = new File(getDataFolder() + "/locale/", "en-US.yml");
        if (!US_File.exists()) {
            US_File.getParentFile().mkdirs();
            saveResource("locale/en-US.yml", false);
            String current_locale = locale();
            Locale locale = Locale.getDefault();
            String language = locale.toLanguageTag();
            if(language.equals("zh-CN") && current_locale.equals("en-US")){
                plugin.getConfig().set("Language", "zh-CN");
                saveConfig();
            }
        }
        String fixed_locale = locale();
        if(fixed_locale.equals("zh-CN")){
            LocaleConfigFile = CN_File;
            LocaleConfig = new YamlConfiguration();
            try {
                LocaleConfig.load(LocaleConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            LocaleConfigFile = US_File;
            LocaleConfig = new YamlConfiguration();
            try {
                LocaleConfig.load(LocaleConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public void createLocaleConfig() {
        if (!CN_File.exists()) {
            CN_File = new File(getDataFolder() + "/locale/", "zh-CN.yml");
            CN_File.getParentFile().mkdirs();
            saveResource("locale/zh-CN.yml", false);
        }
        if (!US_File.exists()) {
            US_File = new File(getDataFolder() + "/locale/", "en-US.yml");
            US_File.getParentFile().mkdirs();
            saveResource("locale/en-US.yml", false);
        }
    }

    public static void reloadLocale_GUI(){
        String Fixed_locale = locale();
        if(LocaleMap.containsKey(Fixed_locale + ".yml")){
            File locale_file = LocaleMap.get(Fixed_locale + ".yml");
            LocaleConfigFile = locale_file;
            reloadGUIConfig();
        } else {
            LocaleConfigFile = US_File;
            reloadGUIConfig();
        }
    }


    public static void reloadGUIConfig(){
        YamlConfiguration guiconfig = YamlConfiguration.loadConfiguration(LocaleConfigFile);
        try {
            guiconfig.save(LocaleConfigFile);
            LocaleConfig = guiconfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initListeners() {
        getServer().getPluginManager().registerEvents((Listener)new BundleListener(), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(), (Plugin)this);
    }

    protected void PluginDisabled() {
        if(locale().equals("zh-CN")){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "插件卸载中...");
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "Plugin disabled...");
        }
        if(locale().equals("zh-CN")){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "已断开与本地数据库连接");
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "Disconnecting the local databases...");
        }
        saveConfig();
    }

    public Connection getMySQLConnection() {
        String host = getConfig().getString("DataBases.host");
        String port = getConfig().getString("DataBases.port");
        String user = getConfig().getString("DataBases.user");
        String password = getConfig().getString("DataBases.password");
        String database = getConfig().getString("DataBases.database");
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + allowPublicKeyRetrieval();
            Connection connection = DriverManager.getConnection(url, user, password);
            if(locale().equals("zh-CN")){
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "成功连接MySQL数据库");
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "Successfully connected to MySQL database.");
            }
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[BetterBundle] " + ChatColor.YELLOW + "Connect to the database failed, Check the MySQL database is enabled.");
            e.printStackTrace();
            return null;
        }
    }
}
