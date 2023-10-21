package org.shadowflare.economy.chestshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class ShopSystem {
    private final ChestShop main;

    public ShopSystem(ChestShop main) {
        this.main = main;
    }

    private boolean delItemInventory(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        int amount = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack invItem : inv.getContents()) {
            if (invItem == null) {
                continue;
            }
            ItemStack copyItem = invItem.clone();
            copyItem.setAmount(1);
            if (copyItem.equals(item)) {
                items.add(invItem);
                amount += invItem.getAmount();
            }
        }
        if (amount < amo) {
            return false;
        }
        amount = amo;
        for (ItemStack i : items) {
            if (i.getAmount() == amount) {
                i.setAmount(0);
                return true;
            } else if (i.getAmount() > amount) {
                i.setAmount(i.getAmount() - amount);
                return true;
            } else {
                amount -= i.getAmount();
                i.setAmount(0);
            }
        }
        return false;
    }

    private boolean hasItem(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        int amount = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack invItem : inv.getContents()) {
            if (invItem == null) {
                continue;
            }
            ItemStack copyItem = invItem.clone();
            copyItem.setAmount(1);
            if (copyItem.equals(item)) {
                items.add(invItem);
                amount += invItem.getAmount();
            }
        }
        return amount >= amo;
    }

    private boolean hasInventoryArea(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        int amount = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                amount += item.getMaxStackSize();
                continue;
            }
            ItemStack copyItem = i.clone();
            copyItem.setAmount(1);
            if (copyItem.equals(item)) {
                amount += 64 - i.getAmount();
                items.add(i);
            }
        }
        return amount >= amo;
    }

    private boolean addItemInventory(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        int amount = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                amount += item.getMaxStackSize();
                continue;
            }
            ItemStack copyItem = i.clone();
            copyItem.setAmount(1);
            if (copyItem.equals(item)) {
                amount += 64 - i.getAmount();
                items.add(i);
            }
        }
        if (amount < amo) {
            return false;
        }
        amount = amo;
        for (ItemStack i : items) {
            if (i.getAmount() == i.getMaxStackSize()) {
                continue;
            }
            if (i.getMaxStackSize() - i.getAmount() == amount) {
                i.setAmount(i.getMaxStackSize());
                return true;
            } else if (i.getMaxStackSize() - i.getAmount() > amount) {
                i.setAmount(i.getAmount() + amount);
                return true;
            } else {
                amount -= i.getAmount();
                i.setAmount(i.getMaxStackSize());
            }
        }
        while (true) {
            if (amount == item.getMaxStackSize()) {
                item.setAmount(item.getMaxStackSize());
                inv.addItem(item);
                return true;
            } else if (amount < item.getMaxStackSize()) {
                item.setAmount(amount);
                inv.addItem(item);
                return true;
            } else {
                item.setAmount(item.getMaxStackSize());
                inv.addItem(item);
                amount -= item.getMaxStackSize();
            }
        }
    }

    public void dropItem(Player player, ItemStack item, int amo) {
        int amount = amo;
        while (true) {
            if (amount > item.getMaxStackSize()) {
                item.setAmount(item.getMaxStackSize());
                amount -= item.getMaxStackSize();
                main.getServer().getWorld(player.getWorld().getName()).dropItemNaturally(player.getLocation(), item).setPickupDelay(0);
            } else {
                item.setAmount(amount);
                main.getServer().getWorld(player.getWorld().getName()).dropItemNaturally(player.getLocation(), item).setPickupDelay(0);
                break;
            }
        }
    }

    public void onSignRightClick(PlayerInteractEvent event, Block block, WallSign data) {
        Chest chest = null;
        switch (data.getFacing()) {
            case EAST:
                chest = (Chest) block.getLocation().add(-1.0, 0.0, 0.0).getBlock().getState();
                break;
            case SOUTH:
                chest = (Chest) block.getLocation().add(0.0, 0.0, -1.0).getBlock().getState();
                break;
            case WEST:
                chest = (Chest) block.getLocation().add(1.0, 0.0, 0.0).getBlock().getState();
                break;
            case NORTH:
                chest = (Chest) block.getLocation().add(0.0, 0.0, 1.0).getBlock().getState();
                break;
        }
        if (!main.getShops().getConfig().contains(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ())) {
            return;
        }
        if (!main.getShops().getConfig().getBoolean(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".bought")) {
            event.getPlayer().sendMessage(main.langToMessage("sellOnly", "This shop is for sell only."));
            return;
        }
        String owner = main.getShops().getConfig().getString(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".owner");
        if (event.getPlayer().getUniqueId().toString().equals(owner)) {
            return;
        }
        ItemStack item = main.getShops().getConfig().getItemStack(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".item");
        int amount = main.getShops().getConfig().getInt(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".amount");
        int boughtPrice = main.getShops().getConfig().getInt(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".price");
        item.setAmount(1);
        if (chest.isAdmin()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            if (main.isEconomy()) {
                if (!main.getEconomy().has(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), boughtPrice)) {
                    event.getPlayer().sendMessage(main.langToMessage("balanceInsufficient", "Your balance is insufficient."));
                    return;
                }
                event.getPlayer().sendMessage(main.langToMessage("boughtItem", "You just bought %item% for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", main.getEconomy().format(boughtPrice)));
                main.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), boughtPrice);
                dropItem(event.getPlayer(), item, amount);
            } else {
                if (delItemInventory(event.getPlayer().getInventory(), main.getCurrency(), boughtPrice)) {
                    dropItem(event.getPlayer(), item, amount);
                    event.getPlayer().sendMessage(main.langToMessage("boughtItem", "You just bought %item% for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", boughtPrice + " Emerald"));
                } else {
                    event.getPlayer().sendMessage(main.langToMessage("emeraldInsufficient", "Your emerald is insufficient."));
                    return;
                }
            }
            return;
        }
        if (main.isEconomy()) {
            if (!main.getEconomy().has(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), boughtPrice)) {
                event.getPlayer().sendMessage(main.langToMessage("balanceInsufficient", "Your balance is insufficient."));
                return;
            }
            event.getPlayer().sendMessage(main.langToMessage("boughtItem", "You just bought %item% for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", main.getEconomy().format(boughtPrice)));
            main.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), boughtPrice);
            main.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)), (boughtPrice / 100) * (100 - main.getConfig().getInt("tax", 0)));
            main.getDatas().getConfig().set("revenue", main.getDatas().getConfig().getInt("revenue", 0) + (int) ((boughtPrice / 100.0) * main.getConfig().getInt("tax", 0)));
            main.getDatas().saveConfig();
            dropItem(event.getPlayer(), item, amount);
        } else {
            if (hasInventoryArea(chest.getInventory(), main.getCurrency(), boughtPrice)) {
                if (hasItem(chest.getInventory(), item, amount)) {
                    if (delItemInventory(event.getPlayer().getInventory(), main.getCurrency(), boughtPrice)) {
                        addItemInventory(chest.snapshotInventory, main.getCurrency(), (boughtPrice / 100) * (100 - main.getConfig().getInt("tax", 0)));
                        main.getDatas().getConfig().set("revenue", main.getDatas().getConfig().getInt("revenue", 0) + (int) ((boughtPrice / 100.0) * main.getConfig().getInt("tax", 0)));
                        delItemInventory(chest.snapshotInventory, item, amount);
                        event.getPlayer().sendMessage(main.langToMessage("boughtItem", "You just bought %item% for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", boughtPrice + " Emerald"));
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        dropItem(event.getPlayer(), item, amount);
                        chest.update();
                        main.getDatas().saveConfig();
                        return;
                    } else {
                        event.getPlayer().sendMessage(main.langToMessage("emeraldInsufficient", "Your emerald is insufficient."));
                        return;
                    }
                } else {
                    event.getPlayer().sendMessage(main.langToMessage("outOfStock", "This shop does not have it in stock. Please wait until it's refilled or contact us at " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName()));
                    return;
                }
            } else {
                event.getPlayer().sendMessage(main.langToMessage("shopNoInventoryBought", "I couldn't bought it because the shop is full. Please wait until the stock is low or contact " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName()));
                return;
            }
        }
    }

    public void onSignLeftClick(PlayerInteractEvent event, Block block, WallSign data) {
        Chest chest = null;
        switch (data.getFacing()) {
            case EAST:
                chest = (Chest) block.getLocation().add(-1.0, 0.0, 0.0).getBlock().getState();
                break;
            case SOUTH:
                chest = (Chest) block.getLocation().add(0.0, 0.0, -1.0).getBlock().getState();
                break;
            case WEST:
                chest = (Chest) block.getLocation().add(1.0, 0.0, 0.0).getBlock().getState();
                break;
            case NORTH:
                chest = (Chest) block.getLocation().add(0.0, 0.0, 1.0).getBlock().getState();
                break;
        }
        if (!main.getShops().getConfig().contains(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ())) {
            return;
        }
        if (!main.getShops().getConfig().getBoolean(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".sell")) {
            event.getPlayer().sendMessage(main.langToMessage("sellOnly", "This shop is for sell only."));
            return;
        }
        String owner = main.getShops().getConfig().getString(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".owner");
        if (event.getPlayer().getUniqueId().toString().equals(owner)) {
            return;
        }
        ItemStack item = main.getShops().getConfig().getItemStack(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".item");
        int amount = main.getShops().getConfig().getInt(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".amount");
        int sellPrice = main.getShops().getConfig().getInt(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".sellPrice");
        item.setAmount(1);
        if (chest.isAdmin()) {
            if (delItemInventory(event.getPlayer().getInventory(), item, amount)) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                if (main.isEconomy()) {
                    event.getPlayer().sendMessage(main.langToMessage("sellItem", "You sold %item% items for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", main.getEconomy().format(sellPrice)));
                    main.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), sellPrice);
                } else {
                    event.getPlayer().sendMessage(main.langToMessage("sellItem", "You sold %item% items for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", sellPrice + " Emerald"));
                    dropItem(event.getPlayer(), main.getCurrency(), sellPrice);
                }
                return;
            } else {
                event.getPlayer().sendMessage(main.langToMessage("notEnoughItem", "Not enough items to sell."));
            }
            return;
        }
        if (main.isEconomy()) {
            if (!hasItem(event.getPlayer().getInventory(), item, amount)) {
                event.getPlayer().sendMessage(main.langToMessage("notEnoughItem", "Not enough items to sell."));
                return;
            }
            if (!main.getEconomy().has(Bukkit.getOfflinePlayer(UUID.fromString(owner)), sellPrice)) {
                event.getPlayer().sendMessage(main.langToMessage("notEnoughMoneyOwner", "The shop owner does not have enough money to sell."));
                return;
            }
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            event.getPlayer().sendMessage(main.langToMessage("sellItem", "You sold %item% items for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", main.getEconomy().format(sellPrice)));
            delItemInventory(event.getPlayer().getInventory(), item, amount);
            main.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), sellPrice);
            main.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)), (sellPrice / 100) * (100 - main.getConfig().getInt("tax", 0)));
            main.getDatas().getConfig().set("revenue", main.getDatas().getConfig().getInt("revenue", 0) + (int) ((sellPrice / 100.0) * main.getConfig().getInt("tax", 0)));
            main.getDatas().saveConfig();
        } else {
            if (hasItem(chest.getInventory(), main.getCurrency(), sellPrice)) {
                if (hasInventoryArea(chest.getInventory(), item, amount)) {
                    if (delItemInventory(event.getPlayer().getInventory(), item, amount)) {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        event.getPlayer().sendMessage(main.langToMessage("sellItem", "You sold %item% items for %price%!").replace("%item%", item.getType().name() + " x" + amount).replace("%price%", sellPrice + " Emerald"));
                        delItemInventory(chest.snapshotInventory, main.getCurrency(), (sellPrice / 100) * (100 - main.getConfig().getInt("tax", 0)));
                        main.getDatas().getConfig().set("revenue", main.getDatas().getConfig().getInt("revenue", 0) + (int) ((sellPrice / 100.0) * main.getConfig().getInt("tax", 0)));
                        addItemInventory(chest.snapshotInventory, item, amount);
                        dropItem(event.getPlayer(), main.getCurrency(), sellPrice);
                        chest.update();
                        main.getDatas().saveConfig();
                    } else {
                        event.getPlayer().sendMessage(main.langToMessage("notEnoughSellItem", "Not enough items to sell"));
                        return;
                    }
                } else {
                    event.getPlayer().sendMessage(main.langToMessage("shopNoInventorySell", "I couldn't sell it because the shop is full. Please wait until the stock is low or contact " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName()));
                    return;
                }
            } else {
                event.getPlayer().sendMessage(main.langToMessage("notFoundEmerald", "Can't sell items because there are no emeralds in the shop. Please wait until the stock is low or contact " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName()));
                return;
            }
        }
    }

    private String langToMessage(String pass, String defaultMsg, boolean prefix) {
        String message = (prefix ? main.getLang().getConfig().getString("prefix", "&7[&6SHOP&7]&f ") : "") + main.getLang().getConfig().getString(pass, defaultMsg);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private boolean isAdmin(Chest chest) {
        return main.getShops().getConfig().getBoolean(chest.getLocation().getWorld().getName() + "-" + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ() + ".isAdmin", false);
    }
}
