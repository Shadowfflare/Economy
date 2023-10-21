package org.shadowflare.economy.chestshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DeleteShop implements Listener {
    private final ChestShop main;

    public DeleteShop(ChestShop main) {
        this.main = main;
    }

    @EventHandler
    public void delete(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.CHEST) {
            if (!main.shops.getConfig().contains(block.getWorld().getName() + "-" + block.getX() + "-" + block.getY() + "-" + block.getZ())) {
                return;
            }

            Chest chest = (Chest) block.getState();
            boolean admin = main.shops.getConfig().getBoolean(block.getWorld().getName() + "-" + block.getX() + "-" + block.getY() + "-" + block.getZ() + ".admin");
            String owner = main.shops.getConfig().getString(block.getWorld().getName() + "-" + block.getX() + "-" + block.getY() + "-" + block.getZ() + ".owner");

            if (admin) {
                if (!event.getPlayer().hasPermission("chestshop.adminshop") || event.getPlayer().hasPermission("chestshop.delete")) {
                    event.getPlayer().sendMessage(main.lang.toMessage("DeleteIsOwnerOnly", "The shop can only be destroyed by the player owner!"));
                    event.setCancelled(true);
                    return;
                }
            } else if (!owner.equals(event.getPlayer().getUniqueId().toString())) {
                event.getPlayer().sendMessage(main.lang.toMessage("DeleteIsOwnerOnly", "The shop can only be destroyed by the player owner!"));
                event.setCancelled(true);
                return;
            }

            for (org.bukkit.entity.Entity i : block.getWorld().getEntities()) {
                if (block.getLocation().add(0.5, 1.0, 0.5).distance(i.getLocation()) <= 0.2) {
                    if (i.getScoreboardTags().contains("ChestShopItemTag")) {
                        chest.setCustomName(null);
                        chest.update();
                        block.breakNaturally();
                        i.remove();
                        event.getPlayer().sendMessage(main.lang.toMessage("DeleteShop", "Deleted a shop!"));
                        main.shops.getConfig().set(block.getWorld().getName() + "-" + block.getX() + "-" + block.getY() + "-" + block.getZ(), null);
                        main.shops.saveConfig();
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        return;
                    }
                }
            }
        } else if (block.getBlockData() instanceof WallSign) {
            Sign sign = (Sign) block.getState();
            WallSign data = (WallSign) block.getBlockData();
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
                default:
                    break;
            }

            if (chest == null || !main.shops.getConfig().contains(chest.getWorld().getName() + "-" + chest.getX() + "-" + chest.getY() + "-" + chest.getZ())) {
                return;
            }

            boolean admin = isAdmin(chest);
            String owner = main.shops.getConfig().getString(chest.getWorld().getName() + "-" + chest.getX() + "-" + chest.getY() + "-" + chest.getZ() + ".owner");

            if (admin) {
                if (!event.getPlayer().hasPermission("chestshop.adminshop")) {
                    event.getPlayer().sendMessage(main.lang.toMessage("DeleteIsOwnerOnly", "The shop can only be destroyed by the player owner!"));
                    return;
                }
            } else if (!owner.equals(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission("chestshop.delete")) {
                event.getPlayer().sendMessage(main.lang.toMessage("DeleteIsOwnerOnly", "The shop can only be destroyed by the player owner!"));
                return;
            }

            for (org.bukkit.entity.Entity i : block.getWorld().getEntities()) {
                if (chest.getLocation().add(0.5, 1.0, 0.5).distance(i.getLocation()) <= 0.2) {
                    if (i.getScoreboardTags().contains("ChestShopItemTag")) {
                        chest.setCustomName(null);
                        chest.update();
                        chest.getBlock().breakNaturally();
                        i.remove();
                        event.getPlayer().sendMessage(main.lang.toMessage("DeleteShop", "Deleted a shop!"));
                        main.shops.getConfig().set(chest.getWorld().getName() + "-" + chest.getX() + "-" + chest.getY() + "-" + chest.getZ(), null);
                        main.shops.saveConfig();
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        return;
                    }
                }
            }
        }
    }

    private boolean isAdmin(Chest chest) {
        return main.shops.getConfig().getBoolean(chest.getWorld().getName() + "-" + chest.getX() + "-" + chest.getY() + "-" + chest.getZ() + ".admin");
    }
}
