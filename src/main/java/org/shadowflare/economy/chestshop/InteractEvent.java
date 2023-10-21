package org.shadowflare.economy.chestshop;

import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {
    private final ChestShop main;

    public InteractEvent(ChestShop main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getBlockData() instanceof WallSign) {
            main.getCreateShop().onSignRightClick(event);
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getBlockData() instanceof WallSign) {
            Sign blockState = (Sign) event.getClickedBlock().getState();
            WallSign data = (WallSign) event.getClickedBlock().getBlockData();
            main.getShopSystem().onSignLeftClick(event, event.getClickedBlock(), data);
        }
    }
}
