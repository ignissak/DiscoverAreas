package net.ignissak.discoverareas.sql.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.sql.Connector;

public class MySQLConnector extends Connector {

    @Override
    public void setupHikari() {
        String host = DiscoverAreasPlugin.getConfiguration().getString("sql.host", "localhost");
        int port = DiscoverAreasPlugin.getConfiguration().getInt("sql.port", 3306);
        String username = DiscoverAreasPlugin.getConfiguration().getString("sql.username", "username");
        String password = DiscoverAreasPlugin.getConfiguration().getString("sql.password", "password");
        String database = DiscoverAreasPlugin.getConfiguration().getString("sql.database", "database");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setConnectionTestQuery("SELECT 1;");
        config.setUsername(username);
        config.setPassword(password);

        this.hikariDataSource = new HikariDataSource(config);
    }

}
