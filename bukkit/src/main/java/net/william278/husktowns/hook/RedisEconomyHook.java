package net.william278.husktowns.hook;

import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import net.william278.husktowns.HuskTowns;
import net.william278.husktowns.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.logging.Level;

public class RedisEconomyHook extends EconomyHook {

    private RedisEconomyAPI redisEconomy;

    public RedisEconomyHook(@NotNull HuskTowns plugin) {
        super(plugin, "RedisEconomy");
    }

    @Override
    public void onEnable() {
        this.redisEconomy = RedisEconomyAPI.getAPI();
        plugin.log(Level.INFO, "Enabled RedisEconomy hook");
    }

    @Override
    public boolean takeMoney(@NotNull OnlineUser user, @NotNull BigDecimal amount, @Nullable String reason) {
        return redisEconomy.getDefaultCurrency().withdrawPlayer(user.getUuid(), user.getUsername(), amount.doubleValue(), reason).transactionSuccess();
    }

    @Override
    public void giveMoney(@NotNull OnlineUser user, @NotNull BigDecimal amount, @Nullable String reason) {
        redisEconomy.getDefaultCurrency().depositPlayer(user.getUuid(), user.getUsername(), amount.doubleValue(), reason);
    }

    @Override
    public String formatMoney(@NotNull BigDecimal amount) {
        return redisEconomy.getDefaultCurrency().format(amount.doubleValue());
    }

}
