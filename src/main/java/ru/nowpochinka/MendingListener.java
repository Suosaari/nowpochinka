package ru.nowpochinka;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class MendingListener implements Listener {

    private static final int EXP_PER_DURABILITY = 10; // 5 опыта за 1 прочность

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int originalExp = event.getAmount();
        
        if (originalExp <= 0) {
            return;
        }

        // Собираем все предметы с починкой, которые можно починить
        List<ItemStack> mendingItems = new ArrayList<>();
        
        // Проверяем броню
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (isValidMendingItem(armor)) {
                mendingItems.add(armor);
            }
        }
        
        // Проверяем предмет в основной руке
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (isValidMendingItem(mainHand)) {
            mendingItems.add(mainHand);
        }
        
        // Проверяем предмет во второй руке
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (isValidMendingItem(offHand)) {
            mendingItems.add(offHand);
        }

        if (mendingItems.isEmpty()) {
            return; // Нет предметов с починкой для ремонта
        }

        // Выбираем случайный предмет для починки (как в оригинале)
        ItemStack itemToMend = mendingItems.get((int) (Math.random() * mendingItems.size()));
        Damageable meta = (Damageable) itemToMend.getItemMeta();
        
        if (meta != null && meta.hasDamage()) {
            int damage = meta.getDamage();
            int expToUse = originalExp;
            
            // Рассчитываем, сколько прочности можем восстановить
            int durabilityToRepair = expToUse / EXP_PER_DURABILITY;
            
            if (durabilityToRepair > 0) {
                // Ограничиваем количество восстанавливаемой прочности текущим уроном
                durabilityToRepair = Math.min(durabilityToRepair, damage);
                
                // Восстанавливаем прочность
                meta.setDamage(damage - durabilityToRepair);
                itemToMend.setItemMeta(meta);
                
                // Вычитаем использованный опыт
                int usedExp = durabilityToRepair * EXP_PER_DURABILITY;
                event.setAmount(originalExp - usedExp);
                
                // Обновляем инвентарь игрока
                player.updateInventory();
            }
        }
    }

    private boolean isValidMendingItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        // Проверяем, есть ли починка
        if (!item.containsEnchantment(Enchantment.MENDING)) {
            return false;
        }
        
        // Проверяем, есть ли урон
        if (item.getItemMeta() instanceof Damageable damageable) {
            return damageable.hasDamage();
        }
        
        return false;
    }
}