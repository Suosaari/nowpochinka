package ru.nowpochinka;

import org.bukkit.plugin.java.JavaPlugin;

public class NowPochinka extends JavaPlugin {

    private static NowPochinka instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Регистрируем слушатель событий
        getServer().getPluginManager().registerEvents(new MendingListener(), this);
        
        getLogger().info("NowPochinka включен! Починка теперь требует 5 опыта за 1 прочность.");
    }

    @Override
    public void onDisable() {
        getLogger().info("NowPochinka выключен!");
    }

    public static NowPochinka getInstance() {
        return instance;
    }
}