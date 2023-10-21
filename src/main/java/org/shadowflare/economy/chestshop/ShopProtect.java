package org.shadowflare.economy.chestshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopProtect implements Listener {
    private final ChestShop main;

    public ShopProtect(ChestShop main) {
        this.main = main;
    }

    @EventHandler
    public void onChestClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
                if (main.getShops().getConfig().contains(
                        event.getClickedBlock().getWorld().getName() + "-"
                                + event.getClickedBlock().getX() + "-"
                                + event.getClickedBlock().getY() + "-"
                                + event.getClickedBlock().getZ())) {
                    boolean admin = main.getShops().getConfig().getBoolean(
                            event.getClickedBlock().getWorld().getName() + "-"
                                    + event.getClickedBlock().getX() + "-"
                                    + event.getClickedBlock().getY() + "-"
                                    + event.getClickedBlock().getZ() + ".admin");
                    if (admin) {
                        event.setCancelled(true);
                        if (event.getPlayer().hasPermission("chestshop.adminshop")) {
                            event.getPlayer().sendMessage(main.langToMessage("adminShopOpenChest",
                                    "The Admin Shop does not need to be replenished with items."));
                            return;
                        }
                    }
                    String owner = main.getShops().getConfig().getString(
                            event.getClickedBlock().getWorld().getName() + "-"
                                    + event.getClickedBlock().getX() + "-"
                                    + event.getClickedBlock().getY() + "-"
                                    + event.getClickedBlock().getZ() + ".owner");
                    if (!owner.equals(event.getPlayer().getUniqueId().toString())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Chest chest = (Chest) event.getBlock().getLocation().add(0, -1, 0).getBlock().getState();
        if (chest.getType() == Material.CHEST && chest.getCustomName() != null
                && chest.getCustomName().startsWith(main.langToMessage("Shop", "&eShop", false))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Chest chest = (Chest) event.getToBlock().getLocation().add(0, -1, 0).getBlock().getState();
        if (chest.getType() == Material.CHEST && chest.getCustomName() != null
                && chest.getCustomName().startsWith(main.langToMessage("Shop", "&eShop", false))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Chest chest1 = (Chest) event.getBlock().getLocation().add(0, -1, 0).getBlock().getState();
        Chest chest2 = (Chest) event.getBlock().getLocation().getBlock().getState();
        if (chest1.getType() == Material.CHEST && chest1.getCustomName() != null
                && chest1.getCustomName().startsWith(main.langToMessage("Shop", "&eShop", false))) {
            event.setCancelled(true);
        } else if (chest2.getType() == Material.CHEST && chest2.getCustomName() != null
                && chest2.getCustomName().startsWith(main.langToMessage("Shop", "&eShop", false))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent event) {
        for (double i = 1; i <= 12; i++) {
            BlockFace direction = event.getDirection();
            Chest chest;
            double x = event.getBlock().getX();
            double y = event.getBlock().getY() - i;
            double z = event.getBlock().getZ();
            switch (direction) {
                case DOWN:
                    chest = (Chest) event.getBlock().getWorld().getBlockAt((int) x, (int) y, (int) z).getState();
                    break;
                case SOUTH:
                    chest = (Chest) event.getBlock().getWorld().getBlockAt((int) x, (int) y, (int) (z + i)).getState();
                    break;
                case NORTH:
                    chest = (Chest) event.getBlock().getWorld().getBlockAt((int) x, (int) y, (int) (z - i)).getState();
                    break;
                case WEST:
                    chest = (Chest) event.getBlock().getWorld().getBlockAt((int) (x - i), (int) y, (int) z).getState();
                    break;
                case EAST:
                    chest = (Chest) event.getBlock().getWorld().getBlockAt((int) (x + i), (int) y, (int) z).getState();
                    break;
                default:
                    return;
            }
            if (chest.getType() == Material.AIR || chest.getType() == Material.VOID_AIR || chest.getType() == Material.CAVE_AIR) {
                if (chest.getCustomName() != null
                        && chest.getCustomName().startsWith(main.langToMessage("Shop", "&eShop", false))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() != null && event.getCaught().getScoreboardTags().contains("ChestShopItemTag")) {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }

    private String langToMessage(String pass, String defaultVal, boolean prefix) {
        String prefixString = prefix ? main.getLang().getConfig().getString("prefix", "&7[&6SHOP&7]&f ") : "";
        return ChatColor.translateAlternateColorCodes('&', prefixString + main.getLang().getConfig().getString(pass, defaultVal));
    }
}
