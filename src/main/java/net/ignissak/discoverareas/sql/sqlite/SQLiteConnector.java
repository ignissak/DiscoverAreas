package net.ignissak.discoverareas.sql.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.sql.Connector;

public class SQLiteConnector extends Connector {

    @Override
    public void setupHikari() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:plugins/" + DiscoverAreasPlugin.getInstance().getDescription().getName() + "/" + DiscoverAreasPlugin.getConfiguration().getString("sql.database", "database") + ".db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setConnectionTestQuery("SELECT 1;");

        this.hikariDataSource = new HikariDataSource(config);
    }
}
