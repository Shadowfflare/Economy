package org.shadowflare.economy.vault.economy;

import java.util.List;
import org.bukkit.OfflinePlayer;

public interface Economy {

    public boolean isEnabled();

    public String getName();

    public boolean hasBankSupport();

    public int fractionalDigits();

    public String format(double amount);

    public String currencyNamePlural();

    public String currencyNameSingular();

    @Deprecated
    public boolean hasAccount(String playerName);

    public boolean hasAccount(OfflinePlayer player);

    @Deprecated
    public boolean hasAccount(String playerName, String worldName);

    public boolean hasAccount(OfflinePlayer player, String worldName);

    @Deprecated
    public double getBalance(String playerName);

    public double getBalance(OfflinePlayer player);

    @Deprecated
    public double getBalance(String playerName, String world);

    public double getBalance(OfflinePlayer player, String world);

    @Deprecated
    public boolean has(String playerName, double amount);

    public boolean has(OfflinePlayer player, double amount);

    @Deprecated
    public boolean has(String playerName, String worldName, double amount);

    public boolean has(OfflinePlayer player, String worldName, double amount);

    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount);

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount);

    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount);

    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount);
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount);

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount);

    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount);

    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount);

    @Deprecated
    public EconomyResponse createBank(String name, String player);

    public EconomyResponse createBank(String name, OfflinePlayer player);

    public EconomyResponse deleteBank(String name);

    public EconomyResponse bankBalance(String name);

    public EconomyResponse bankHas(String name, double amount);

    public EconomyResponse bankWithdraw(String name, double amount);

    public EconomyResponse bankDeposit(String name, double amount);

    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName);

    public EconomyResponse isBankOwner(String name, OfflinePlayer player);

    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName);

    public EconomyResponse isBankMember(String name, OfflinePlayer player);

    public List<String> getBanks();

    @Deprecated
    public boolean createPlayerAccount(String playerName);

    public boolean createPlayerAccount(OfflinePlayer player);
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName);
    public boolean createPlayerAccount(OfflinePlayer player, String worldName);
}