package org.shadowflare.economy.chestshop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WithdrawItem {
    public boolean withdraw(Player player, ItemStack item, int amount) {
        ItemStack cloneItem = item.clone();
        cloneItem.setAmount(1);
        int remainingAmount = amount;
        List<ItemStack> items = new ArrayList<>();
        int invAmount = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null) {
                continue;
            }
            ItemStack invItem = i.clone();
            invItem.setAmount(1);
            if (invItem.equals(cloneItem)) {
                invAmount += i.getAmount();
                items.add(i);
            }
        }
        if (amount == 0) {
            return true;
        }
        if (invAmount < amount) {
            return false;
        }
        for (ItemStack i : items) {
            if (remainingAmount == i.getAmount()) {
                i.setAmount(0);
                return true;
            } else if (remainingAmount > i.getAmount()) {
                remainingAmount -= i.getAmount();
                i.setAmount(0);
            } else if (remainingAmount < i.getAmount()) {
                i.setAmount(i.getAmount() - remainingAmount);
                return true;
            }
        }
        return false;
    }
}
