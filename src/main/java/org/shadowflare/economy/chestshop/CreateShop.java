package org.shadowflare.economy.chestshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CreateShop implements Listener {
    private final ChestShop main;

    public CreateShop(ChestShop main) {
        this.main = main;
    }

    @EventHandler
    public void onSignRightClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign)) {
            return;
        }

        Sign blockState = (Sign) block.getState();
        WallSign data = (WallSign) block.getBlockData();

        if (blockState.getLine(0).equalsIgnoreCase("[shop]") || blockState.getLine(0).equalsIgnoreCase("[adminshop]")) {
            boolean admin = blockState.getLine(0).equalsIgnoreCase("[adminshop]");

            if (admin && !event.getPlayer().hasPermission("chestshop.adminshop")) {
                return;
            }

            if (main.economy) {
                if (!main.econ.has(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), main.getConfig().getInt("creationCost"))) {
                    event.getPlayer().sendMessage(main.lang.toMessage("NotEnoughCost", "The shop creation cost is not enough."));
                    return;
                }

                main.econ.withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), main.getConfig().getInt("creationCost"));
            } else {
                if (!main.withdrawItem.withdraw(event.getPlayer(), new ItemStack(Material.EMERALD), main.getConfig().getInt("creationCost"))) {
                    event.getPlayer().sendMessage(main.lang.toMessage("NotEnoughCost", "The shop creation cost is not enough."));
                    return;
                }
            }

            main.datas.getConfig().set("revenue", main.datas.getConfig().getInt("revenue") + 200);

            int itemAmount = Integer.parseInt(blockState.getLine(1));
            if (itemAmount < 1) {
                event.getPlayer().sendMessage(main.lang.toMessage("MinItemAmount", "The number of items must be at least 1."));
                return;
            }

            int sellPrice = Integer.parseInt(blockState.getLine(3));
            if (sellPrice < 1) {
                event.getPlayer().sendMessage(main.lang.toMessage("MinSellPrice", "The sell price should be set at 0 or more."));
                return;
            }

            int boughtPrice = Integer.parseInt(blockState.getLine(2));
            if (boughtPrice < 1) {
                event.getPlayer().sendMessage(main.lang.toMessage("MinBoughtPrice", "The bought price should be set at 1 or more."));
                return;
            }

            if (boughtPrice == -1 && sellPrice == -1) {
                event.getPlayer().sendMessage(main.lang.toMessage("NotSetPrice", "To create a shop, you'll need to set a price for a sell or bought, or both!"));
                return;
            }

            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                event.getPlayer().sendMessage(main.lang.toMessage("NotSetItem", "To create a shop you need to hold the item you want to sell in your hand and right-click on the sign"));
                return;
            }

            ItemStack item = event.getPlayer().getInventory().getItemInMainHand().clone();
            item.setAmount(1);

            Chest chest;
            BlockFace face;

            switch (data.getFacing()) {
                case EAST:
                    if (block.getRelative(-1, 0, 0).getType() == Material.CHEST || block.getRelative(-1, 0, 0).getType() == Material.TRAPPED_CHEST) {
                        chest = (Chest) block.getRelative(-1, 0, 0).getState();
                        face = BlockFace.EAST;
                    } else {
                        return;
                    }
                    break;
                case SOUTH:
                    if (block.getRelative(0, 0, -1).getType() == Material.CHEST || block.getRelative(0, 0, -1).getType() == Material.TRAPPED_CHEST) {
                        chest = (Chest) block.getRelative(0, 0, -1).getState();
                        face = BlockFace.SOUTH;
                    } else {
                        return;
                    }
                    break;
                case WEST:
                    if (block.getRelative(1, 0, 0).getType() == Material.CHEST || block.getRelative(1, 0, 0).getType() == Material.TRAPPED_CHEST) {
                        chest = (Chest) block.getRelative(1, 0, 0).getState();
                        face = BlockFace.WEST;
                    } else {
                        return;
                    }
                    break;
                case NORTH:
                    if (block.getRelative(0, 0, 1).getType() == Material.CHEST || block.getRelative(0, 0, 1).getType() == Material.TRAPPED_CHEST) {
                        chest = (Chest) block.getRelative(0, 0, 1).getState();
                        face = BlockFace.NORTH;
                    } else {
                        return;
                    }
                    break;
                default:
                    return;
            }

            if (chest.getRelative(0, 1, 0).getType() != Material.AIR) {
                event.getPlayer().sendMessage(main.lang.toMessage("ChestTop", "Can't create a shop because there is a block on top of the chest"));
                return;
            }

            org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
            if (chestData.getFacing() != face) {
                event.getPlayer().sendMessage(main.lang.toMessage("SignNeedFront", "The sign needs to be placed on the front of the chest!"));
                return;
            }

            chest.setCustomName("§eShopChest - x" + chest.getBlockX() + " y" + chest.getBlockY() + " z" + chest.getBlockZ());
            chest.update();

            event.getPlayer().getWorld().dropItem(chest.getLocation().add(0.5, 1.2, 0.5), item).setVelocity(new Vector(0, 0.1, 0));
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".item", item);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".price", boughtPrice);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".sellPrice", sellPrice);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".amount", itemAmount);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".bought", boughtPrice >= 0);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".sell", sellPrice >= 0);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".admin", admin);
            main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getBlockX() + "-" + chest.getBlockY() + "-" + chest.getBlockZ() + ".owner", event.getPlayer().getUniqueId().toString());
            main.shops.saveConfig();

            blockState.setLine(1, (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() + " ×" + itemAmount : item.getType().name() + " ×" + itemAmount);
            blockState.setLine(0, admin ? main.lang.toMessage("adminshop", "§c§lAdmin SHOP", false) : main.lang.toMessage("shop", "§e§lSHOP", false));

            if (boughtPrice >= 0) {
                blockState.setLine(2, main.lang.toMessage("boughtShop", "B: &r" + boughtPrice, false));
                if (sellPrice >= 0) {
                    blockState.setLine(2, main.lang.toMessage("bothshop", "B &r" + boughtPrice + " : &r" + sellPrice + " S", false));
                }
            } else {
                blockState.setLine(2, main.lang.toMessage("sellShop", "S &r" + sellPrice, false));
            }

            blockState.setLine(3, admin ? "§lAdminister" : "§l" + event.getPlayer().getName());
            blockState.update();

            event.getPlayer().sendMessage(main.lang.toMessage("CreateShop", "Created a shop!"));
        } else {
            main.shopSystem.onSignRightClick(event, block, data);
        }
    }

    private String toMessage(String pass, String defaultVal, boolean prefix) {
        String prefixStr = (prefix) ? main.lang.getConfig().getString("prefix", "&7[&6SHOP&7]&f ") : "";
        return ChatColor.translateAlternateColorCodes('&', prefixStr + main.lang.getConfig().getString(pass, defaultVal));
    }
}
