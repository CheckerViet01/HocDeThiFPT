package com.example.websome.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@Configuration
public class DataSourceConfig {

    private static final String SQL_SERVER_HOST = "127.0.0.1";
    private static final int SQL_SERVER_PORT = 1433;
    private static final String DB_NAME = "HocDeThiFPT";
    private static final String USER = "sa";
    private static final String PASS = "sa123456Password!";

    @Bean
    @Primary
    public DataSource dataSource() {
        boolean sqlServerAvailable = isSqlServerPortOpen(SQL_SERVER_HOST, SQL_SERVER_PORT, 1500);

        if (sqlServerAvailable) {
            log.info(">>> SUCCESS: Detected SQL Server listening on port 1433! Connecting to SQL Server database [{}]...", DB_NAME);
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true;");
            config.setUsername(USER);
            config.setPassword(PASS);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(15000);
            config.setPoolName("SqlServerHikariPool");
            return new HikariDataSource(config);
        } else {
            log.warn(">>> NOTICE: SQL Server port 1433 is not yet open (SQL Server needs a quick Restart in SSMS to bind TCP 1433).");
            log.info(">>> Initializing resilient Fallback DataSource with full schema & seed data so you can test immediately!");
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.h2.Driver");
            config.setJdbcUrl("jdbc:h2:mem:HocDeThiFPT;DB_CLOSE_DELAY=-1;MODE=MSSQLServer");
            config.setUsername("sa");
            config.setPassword("");
            config.setPoolName("H2FallbackPool");
            return new HikariDataSource(config);
        }
    }

    private boolean isSqlServerPortOpen(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
