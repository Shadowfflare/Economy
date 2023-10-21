package org.shadowflare.economy.chestshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.shadowflare.economy.vault.economy.Economy;

public class ChestShop extends JavaPlugin implements Listener {
    private Config lang;
    private Config shops;
    private Config datas;
    private Economy econ;
    private boolean economy;
    private ShopProtect shopProtect;
    private InteractEvent interactEvent;
    private CreateShop createShop;
    private ShopSystem shopSystem;
    private WithdrawItem withdrawItem;
    private ItemStack currency;
    private DeleteShop deleteShop;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        lang = new Config(this, "languages.yml");
        shops = new Config(this, "shops.yml");
        datas = new Config(this, "datas.yml");
        shopProtect = new ShopProtect(this);
        interactEvent = new InteractEvent(this);
        createShop = new CreateShop(this);
        shopSystem = new ShopSystem(this);
        withdrawItem = new WithdrawItem();
        currency = new ItemStack(Material.EMERALD);
        deleteShop = new DeleteShop(this);

        Bukkit.getPluginManager().registerEvents(interactEvent, this);
        Bukkit.getPluginManager().registerEvents(shopProtect, this);
        Bukkit.getPluginManager().registerEvents(deleteShop, this);

        if (getConfig().getString("mode").equals("economy")) {
            if (setupEconomy()) {
                economy = true;
            } else {
                getLogger().severe(lang.toMessage("NotFoundVault", "&cI'm currently set to \"Economy\" mode in the config, but I couldn't find a vault or an economy plugin to support the vault and couldn't start it correctly. If you do not plan to install an economy plugin, please change to \"Emerald\" mode"));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            Material temp = Material.getMaterial(getConfig().getString("currency").toUpperCase());
            if (temp != null) {
                currency = new ItemStack(temp);
            } else {
                getLogger().log.severe(String.format("notFoundItem", "The currency was set to emerald because the item name %name% was not found.").replace("%name%", getConfig().getString("currency")));
            }
        }

        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (org.bukkit.entity.Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains("ChestShopItemTag")) {
                    if (entity instanceof Item) {
                        Item item = (Item) entity;
                        item.setPickupDelay(Integer.MAX_VALUE);
                        item.setTicksLived(Integer.MAX_VALUE);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("chestshop")) {
            if (args.length == 0) {
                sender.sendMessage(lang.toMessage("help", "&6/chestshop reload&8: &7Reload the config file.", false));
                return true;
            }
            switch (args[0]) {
                case "reload":
                    if (sender.hasPermission("chestshop.reload")) {
                        return true;
                    }
                    sender.sendMessage(lang.toMessage("reloadDone", "&6Reload is complete."));
                    lang.reloadConfig();
                    shops.reloadConfig();
                    reloadConfig();
                    datas.reloadConfig();
                    if (getConfig().getString("mode").equals("economy")) {
                        if (setupEconomy()) {
                            economy = true;
                        } else {
                            getLogger().severe(lang.toMessage("NotFoundVault", "&cI'm currently set to \"Economy\" mode in the config, but I couldn't find a vault or an economy plugin to support the vault and couldn't start it correctly. If you do not plan to install an economy plugin, please change to \"Emerald\" mode"));
                            getServer().getPluginManager().disablePlugin(this);
                        }
                    } else {
                        Material temp = Material.getMaterial(getConfig().getString("currency").toUpperCase());
                        if (temp != null) {
                            currency = new ItemStack(temp);
                        } else {
                            getLogger().log.severe(String.format("notFoundItem", "The currency was set to emerald because the item name %name% was not found.").replace("%name%", getConfig().getString("currency")));
                        }
                    }
                    break;
                case "revenue":
                    if (args.length == 1) {
                        sender.sendMessage(lang.toMessage("currentRevenue", "Current tax revenue: &a" + datas.config().getInt("revenue")));
                        return true;
                    } else if (args.length == 2) {
                        switch (args[1]) {
                            case "view":
                                if (sender.hasPermission("chestshop.revenue.view")) {
                                    sender.sendMessage(lang.toMessage("hasNotPermission", "You do not have permission to execute this command."));
                                    return true;
                                }
                                sender.sendMessage(lang.toMessage("currentRevenue", "Current tax revenue: &a" + datas.config().getInt("revenue"));
                                return true;
                            case "get":
                                if (sender.hasPermission("chestshop.revenue.get")) {
                                    sender.sendMessage(lang.toMessage("hasNotPermission", "You do not have permission to execute this command."));
                                    return true;
                                }
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    if (economy) {
                                        econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), datas.config().getInt("revenue"));
                                    } else {
                                        shopSystem.dropItem(player, currency, datas.config().getInt("revenue"));
                                    }
                                    datas.config().set("revenue", 0);
                                    datas.saveConfig();
                                    sender.sendMessage(lang.toMessage("passTax", "Pass all tax revenue to &a" + player.getName()));
                                    return true;
                                } else {
                                    sender.sendMessage(lang.toMessage("commandPlayerOnly", "This command can only be executed by a player."));
                                    return true;
                                }
                        }
                    }
                default:
                    sender.sendMessage(lang.toMessage("help", "&6/chestshop reload&8: &7Reload the config file.", false));
                    break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commands = Arrays.asList("reload", "revenue");
        List<String> revenue = Arrays.asList("get", "view");
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equals("chestshop")) {
            if (args.length == 1) {
                if (args[0].isEmpty()) {
                    suggestions.addAll(commands);
                } else {
                    for (String cmd : commands) {
                        if (cmd.startsWith(args[0])) {
                            suggestions.add(cmd);
                        }
                    }
                }
            } else if (args.length == 2) {
                if (args[1].isEmpty()) {
                    suggestions.addAll(revenue);
                } else {
                    for (String cmd : revenue) {
                        if (cmd.startsWith(args[1])) {
                            suggestions.add(cmd);
                        }
                    }
                }
            }
        }
        return suggestions;
    }

    private Thread thread = new Thread(() -> {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (org.bukkit.World world : Bukkit.getWorlds()) {
                for (org.bukkit.entity.Entity entity : world.getEntities()) {
                    if (entity.getScoreboardTags().contains("ChestShopItemTag")) {
                        if (entity instanceof Item) {
                            Item item = (Item) entity;
                            item.setPickupDelay(Integer.MAX_VALUE);
                            item.setTicksLived(Integer.MAX_VALUE);
                        }
                    }
                }
            }
        }, 0L, 36000L);
    });

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
        }
        return econ != null;
    }

    private String toMessage(Config config, String pass, String defaultText, boolean prefix) {
        String prefixText = prefix ? lang.config().getString("prefix", "&7[&6SHOP&7]&f ") : "";
        return ChatColor.translateAlternateColorCodes('&', prefixText + config.config().getString(pass, defaultText));
    }
}