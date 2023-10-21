package org.shadowflare.economy.vault.permission;

import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;


public abstract class Permission {

    protected static final Logger log = Logger.getLogger("Minecraft");
    protected Plugin plugin = null;

    abstract public String getName();

    abstract public boolean isEnabled();

    abstract public boolean hasSuperPermsCompat();

    @Deprecated
    public boolean has(String world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world, player, permission);
    }

    @Deprecated
    public boolean has(World world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world.getName(), player, permission);
    }

    public boolean has(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public boolean has(Player player, String permission) {
        return player.hasPermission(permission);
    }

    @Deprecated
    abstract public boolean playerHas(String world, String player, String permission);
    @Deprecated
    public boolean playerHas(World world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world.getName(), player, permission);
    }

    public boolean playerHas(String world, OfflinePlayer player, String permission) {
        if (world == null) {
            return has((String) null, player.getName(), permission);
        }
        return has(world, player.getName(), permission);
    }

    public boolean playerHas(Player player, String permission) {
        return has(player, permission);
    }

    @Deprecated
    abstract public boolean playerAdd(String world, String player, String permission);

    @Deprecated
    public boolean playerAdd(World world, String player, String permission) {
        if (world == null) {
            return playerAdd((String) null, player, permission);
        }
        return playerAdd(world.getName(), player, permission);
    }

    public boolean playerAdd(String world, OfflinePlayer player, String permission) {
        if (world == null) {
            return playerAdd((String) null, player.getName(), permission);
        }
        return playerAdd(world, player.getName(), permission);
    }

    public boolean playerAdd(Player player, String permission) {
        return playerAdd(player.getWorld().getName(), player, permission);
    }

    public boolean playerAddTransient(OfflinePlayer player, String permission) throws UnsupportedOperationException {
        if (player.isOnline()) {
            return playerAddTransient((Player) player, permission);
        }
        throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
    }

    public boolean playerAddTransient(Player player, String permission) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
                paInfo.getAttachment().setPermission(permission, true);
                return true;
            }
        }

        PermissionAttachment attach = player.addAttachment(plugin);
        attach.setPermission(permission, true);

        return true;
    }

    public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission) {
        return playerAddTransient(player, permission);
    }

    public boolean playerAddTransient(String worldName, Player player, String permission) {
        return playerAddTransient(player, permission);
    }

    public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission) {
        return playerRemoveTransient(player, permission);
    }

    public boolean playerRemoveTransient(String worldName, Player player, String permission) {
        return playerRemoveTransient((OfflinePlayer) player, permission);
    }

    @Deprecated
    abstract public boolean playerRemove(String world, String player, String permission);

    public boolean playerRemove(String world, OfflinePlayer player, String permission) {
        if (world == null) {
            return playerRemove((String) null, player.getName(), permission);
        }
        return playerRemove(world, player.getName(), permission);
    }

    @Deprecated
    public boolean playerRemove(World world, String player, String permission) {
        if (world == null) {
            return playerRemove((String) null, player, permission);
        }
        return playerRemove(world.getName(), player, permission);
    }

    public boolean playerRemove(Player player, String permission) {
        return playerRemove(player.getWorld().getName(), player, permission);
    }

    public boolean playerRemoveTransient(OfflinePlayer player, String permission) {
        if (player.isOnline()) {
            return playerRemoveTransient((Player) player, permission);
        } else {
            return false;
        }
    }

    public boolean playerRemoveTransient(Player player, String permission) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
                paInfo.getAttachment().unsetPermission(permission);
                return true;
            }
        }
        return false;
    }

    abstract public boolean groupHas(String world, String group, String permission);

    public boolean groupHas(World world, String group, String permission) {
        if (world == null) {
            return groupHas((String) null, group, permission);
        }
        return groupHas(world.getName(), group, permission);
    }

    abstract public boolean groupAdd(String world, String group, String permission);

    public boolean groupAdd(World world, String group, String permission) {
        if (world == null) {
            return groupAdd((String) null, group, permission);
        }
        return groupAdd(world.getName(), group, permission);
    }

    abstract public boolean groupRemove(String world, String group, String permission);

    public boolean groupRemove(World world, String group, String permission) {
        if (world == null) {
            return groupRemove((String) null, group, permission);
        }
        return groupRemove(world.getName(), group, permission);
    }

    @Deprecated
    abstract public boolean playerInGroup(String world, String player, String group);

    @Deprecated
    public boolean playerInGroup(World world, String player, String group) {
        if (world == null) {
            return playerInGroup((String) null, player, group);
        }
        return playerInGroup(world.getName(), player, group);
    }

    public boolean playerInGroup(String world, OfflinePlayer player, String group) {
        if (world == null) {
            return playerInGroup((String) null, player.getName(), group);
        }
        return playerInGroup(world, player.getName(), group);
    }

    public boolean playerInGroup(Player player, String group) {
        return playerInGroup(player.getWorld().getName(), player, group);
    }

    @Deprecated
    abstract public boolean playerAddGroup(String world, String player, String group);

    @Deprecated
    public boolean playerAddGroup(World world, String player, String group) {
        if (world == null) {
            return playerAddGroup((String) null, player, group);
        }
        return playerAddGroup(world.getName(), player, group);
    }

    public boolean playerAddGroup(String world, OfflinePlayer player, String group) {
        if (world == null) {
            return playerAddGroup((String) null, player.getName(), group);
        }
        return playerAddGroup(world, player.getName(), group);
    }

    public boolean playerAddGroup(Player player, String group) {
        return playerAddGroup(player.getWorld().getName(), player, group);
    }

    @Deprecated
    abstract public boolean playerRemoveGroup(String world, String player, String group);

    @Deprecated
    public boolean playerRemoveGroup(World world, String player, String group) {
        if (world == null) {
            return playerRemoveGroup((String) null, player, group);
        }
        return playerRemoveGroup(world.getName(), player, group);
    }

    public boolean playerRemoveGroup(String world, OfflinePlayer player, String group) {
        if (world == null) {
            return playerRemoveGroup((String) null, player.getName(), group);
        }
        return playerRemoveGroup(world, player.getName(), group);
    }

    public boolean playerRemoveGroup(Player player, String group) {
        return playerRemoveGroup(player.getWorld().getName(), player, group);
    }

    @Deprecated
    abstract public String[] getPlayerGroups(String world, String player);

    @Deprecated
    public String[] getPlayerGroups(World world, String player) {
        if (world == null) {
            return getPlayerGroups((String) null, player);
        }
        return getPlayerGroups(world.getName(), player);
    }

    public String[] getPlayerGroups(String world, OfflinePlayer player) {
        return getPlayerGroups(world, player.getName());
    }

    public String[] getPlayerGroups(Player player) {
        return getPlayerGroups(player.getWorld().getName(), player);
    }

    @Deprecated
    abstract public String getPrimaryGroup(String world, String player);

    @Deprecated
    public String getPrimaryGroup(World world, String player) {
        if (world == null) {
            return getPrimaryGroup((String) null, player);
        }
        return getPrimaryGroup(world.getName(), player);
    }

    public String getPrimaryGroup(String world, OfflinePlayer player) {
        return getPrimaryGroup(world, player.getName());
    }

    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getWorld().getName(), player);
    }

    abstract public String[] getGroups();

    abstract public boolean hasGroupSupport();
}