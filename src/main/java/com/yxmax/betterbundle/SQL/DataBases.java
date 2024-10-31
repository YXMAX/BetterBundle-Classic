package com.yxmax.betterbundle.SQL;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

import static com.yxmax.betterbundle.BetterBundle.*;
import static com.yxmax.betterbundle.Util.MessageUtil.*;
import static com.yxmax.betterbundle.Util.PluginUtil.*;

public class DataBases {

    public static Connection con;

    public static String allowPublicKeyRetrieval(){
        Boolean publickey = plugin.getConfig().getBoolean("DataBases.allowPublicKeyRetrieval");
        if(publickey && mysql){
            String key = "allowPublicKeyRetrieval=true";
            return key;
        }
        return null;
    }

    public static Boolean mysql = plugin.getConfig().getBoolean("DataBases.MySQL");

    public static void setupDataBaseConnection(){
        if(mysql){
            con = plugin.getMySQLConnection();
            ConsoleMsg(ChatColor.YELLOW + "成功连接MySQL数据库");
            MySQLScheduler.Mysqlconnect();
            try {
                MysqlcreateTable(con);
                MysqlcreateCoinTable(con);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(!mysql){
            try {
                con = getConnection();
                createTable(con);
                createCoinTable(con);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() throws Exception {
        SQLiteConfig config = new SQLiteConfig();
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
        SQLiteDataSource ds = new SQLiteDataSource(config);
        String url = System.getProperty("user.dir");
        ds.setUrl("jdbc:sqlite:"+url+"/plugins/BetterBundle/"+"Database.db");
        return ds.getConnection();
    }

    public static void createTable(Connection con)throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS bundle_data (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + "	slot integer,\n"
                + " level integer, \n"
                + " maxslot integer, \n"
                + " upgrade boolean, \n"
                + "	owner string,\n"
                + "	inv string,\n"
                + " ability string\n"
                + ");";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        stat.close();
    }

    public static void createCoinTable(Connection con)throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS bundle_coin (\n"
                + "	uuid string,\n"
                + "	coin bigint,\n"
                + " point integer\n"
                + ");";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        stat.close();
    }

    public static void dropTable(Connection con)throws Exception {
        String sql = "drop table bundle_data ";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        stat.close();
    }

    public static int insertBundle(Connection con, Integer slot, Integer level, Boolean upgrade, String owner, Integer maxslot, String Inv,String ability)throws Exception {
        String sql = "insert into bundle_data (slot, level, maxslot, upgrade, owner, inv, ability) values(?,?,?,?,?,?,?)";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, slot);
        pst.setInt(2, level);
        pst.setInt(3, maxslot);
        pst.setBoolean(4, upgrade);
        pst.setString(5, owner);
        pst.setString(6, Inv);
        pst.setString(7, ability);
        pst.execute();
        Integer sqlid = null;
        Integer return_id = null;
        ResultSet rs = pst.getGeneratedKeys();
        if(rs.next()){
            sqlid = rs.getInt(1);
            Player p = Bukkit.getPlayerExact(owner);
            return_id = sqlid;
        }
        pst.close();
        return return_id;
    }

    public static void updateinv(Connection con,  Integer id, String Inv, String owner)throws Exception {
        String sql = "update bundle_data set inv = ? , owner = ? where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, Inv);
        pst.setString(2, owner);
        pst.setInt(3, id);
        pst.executeUpdate();
        pst.close();
        sendDebug(owner + " save bundle inventory success: " + id);
    }

    public static void updateAbility(Connection con, String list, Integer id) throws Exception{
        String sql = "update bundle_data set ability = ? where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, list);
        pst.setInt(2, id);
        pst.executeUpdate();
        pst.close();
        sendDebug("Save bundle ability list success: " + id);
    }

    public static void UpdateLevel(Connection con,  Integer id, Integer level)throws Exception {
        String sql = "update bundle_data set level = ? where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, level);
        pst.setInt(2, id);
        pst.executeUpdate();
        pst.close();
    }

    public static void UpdateCoin(Connection con,  String uuid, Integer coin)throws Exception {
        String sql = "update bundle_coin set coin = ? where uuid = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, coin);
        pst.setString(2, uuid);
        pst.executeUpdate();
        pst.close();
    }

    public static void CoinAlter(Connection con, OfflinePlayer player, Integer num)throws Exception {
        String uuid = String.valueOf(player.getUniqueId());
        String sql = "update bundle_coin set coin=coin + ? where uuid= ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, num);
        pst.setString(2,uuid);
        pst.executeUpdate();
        pst.close();
    }


    public static String InvCode(Connection con, Integer id)throws Exception {
        String invcode = null;
        String sql = "select inv from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            String invbase = rs.getString("inv");
            invcode = invbase;
        }
        rs.close();
        pst.close();
        return invcode;
    }

    public static Boolean CheckBundleisExist(Connection con, Integer id)throws Exception {
        Boolean bool = false;
        String sql = "select id from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if(rs.next()){
            bool = true;
        }
        rs.close();
        pst.close();
        return bool;
    }

    public static Integer GetSlot(Connection con, Integer id)throws Exception {
        Integer slotcode = null;
        String sql = "select slot from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            Integer slot = rs.getInt("slot");
            slotcode = slot;
        }
        rs.close();
        pst.close();
        return slotcode;
    }

    public static Integer GetMaxSlot(Connection con, Integer id)throws Exception {
        Integer slotcode = null;
        String sql = "select maxslot from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            Integer slot = rs.getInt("maxslot");
            slotcode = slot;
        }
        rs.close();
        pst.close();
        return slotcode;
    }

    public static Integer GetLevel(Connection con, Integer id)throws Exception {
        Integer levelcode = null;
        String sql = "select level from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            Integer level = rs.getInt("level");
            levelcode = level;
        }
        rs.close();
        pst.close();
        return levelcode;
    }

    public static Boolean GetUpgrade(Connection con, Integer id)throws Exception {
        Boolean upgrade = false;
        String sql = "select upgrade from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            Boolean up = rs.getBoolean("upgrade");
            upgrade = up;
        }
        rs.close();
        pst.close();
        return upgrade;
    }

    public static Integer GetCoin(Connection con, String uuid)throws Exception {
        Integer coincode = null;
        String sql = "select coin from bundle_coin where uuid = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, uuid);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            Integer coin = rs.getInt("coin");
            coincode = coin;
        }
        rs.close();
        pst.close();
        return coincode;
    }


    public static void SelectAllInfoOnline(Connection con, String owner, Player player)throws Exception {
        String prefix = prefix();
        String sql = "select id, slot, level, maxslot, upgrade from bundle_data where owner = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, owner);
        ResultSet rs = pst.executeQuery();
        if(rs.next()){
            player.sendMessage(color(prefix + Info_title(owner)));
            int id = rs.getInt("id");
            int slot = rs.getInt("slot");
            Integer level = rs.getInt("level");
            Integer maxslot = rs.getInt("maxslot");
            Boolean upgrade = rs.getBoolean("upgrade");
            player.sendMessage(color(Info_detail(id,slot,level,maxslot,upgrade)));
        } else if(!rs.next()){
            player.sendMessage(color(prefix + info_fail(owner)));
        }
        while(rs.next()){
            int id = rs.getInt("id");
            int slot = rs.getInt("slot");
            Integer level = rs.getInt("level");
            Integer maxslot = rs.getInt("maxslot");
            Boolean upgrade = rs.getBoolean("upgrade");
            player.sendMessage(color(Info_detail(id,slot,level,maxslot,upgrade)));
        }
        rs.close();
        pst.close();
    }

    public static void SelectAllInfo(Connection con, String owner)throws Exception {
        String prefix = prefix();
        String sql = "select id, slot, level, maxslot, upgrade from bundle_data where owner = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, owner);
        ResultSet rs = pst.executeQuery();
        if(rs.next()){
            ConsoleMsg(color(prefix + Info_title(owner)));
            int id = rs.getInt("id");
            int slot = rs.getInt("slot");
            Integer level = rs.getInt("level");
            Integer maxslot = rs.getInt("maxslot");
            Boolean upgrade = rs.getBoolean("upgrade");
            ConsoleMsg(color(Info_detail(id,slot,level,maxslot,upgrade)));
        } else if(!rs.next()){
            ConsoleMsg(color(info_fail(owner)));
        }
        while(rs.next()){
            int id = rs.getInt("id");
            int slot = rs.getInt("slot");
            Integer level = rs.getInt("level");
            Integer maxslot = rs.getInt("maxslot");
            Boolean upgrade = rs.getBoolean("upgrade");
            ConsoleMsg(color(Info_detail(id,slot,level,maxslot,upgrade)));
        }
        rs.close();
        pst.close();
    }

    public static void CheckInv(Connection con, Integer id, Player player)throws Exception {
        String sql = "select inv, owner from bundle_data where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if(rs.next()){
            String invbase = rs.getString("inv");
            String owner = rs.getString("owner");
            String put = owner + ":" + id;
            openCheckingBundleTemp.put(player.getUniqueId(),put);
            player.sendMessage(color(PluginPrefix + check_success(id)));
            player.openInventory(inventoryFromBase64_Check(invbase,id));
        } else {
            player.sendMessage(color(PluginPrefix + check_unknown_id(id)));
        }
        rs.close();
        pst.close();
    }

    public static boolean hasData(Connection con, String uuid)throws Exception {
        String hasData = null;
        String sql = "select uuid from bundle_coin where uuid = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, uuid);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            String Data = rs.getString("uuid");
            hasData = Data;
        }
        if(uuid.equals(hasData)){
            rs.close();
            pst.close();
            return true;
        } else {
            rs.close();
            pst.close();
            return false;
        }
    }

    public static void CoinInsert(Connection con, String uuid, Integer coin, Integer point)throws Exception {
        String sql = "insert into bundle_coin (uuid, coin, point) values(?,?,?)";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        pst.setString(1, uuid);
        pst.setInt(2, coin);
        pst.setInt(3, point);
        pst.execute();
        pst.close();

    }

    public static void MysqlcreateTable(Connection con)throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS bundle_data"
                + "("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,"
                + "slot INT,"
                + "level INT,"
                + "maxslot INT,"
                + "upgrade BOOL,"
                + "owner TEXT,"
                + "inv LONGTEXT,"
                + "ability TEXT"
                + ");";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        stat.close();
    }

    public static void MysqlcreateCoinTable(Connection con)throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS bundle_coin"
                + "("
                + "uuid VARCHAR(255),"
                + "coin BIGINT,"
                + "point BIGINT"
                + ");";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        stat.close();
    }

}
