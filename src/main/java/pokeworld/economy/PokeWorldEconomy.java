package pokeworld.economy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PokeWorldEconomy.MOD_ID)
public class PokeWorldEconomy {

    public static final String MOD_ID = "pokeworld_economy";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /**
     * Currency conversion constants (all values in bronze coins = CobbleDollars).
     * 1 CobbleDollar == 1 bronze_coin.
     */
    public static final long BRONZE_PER_SILVER = 100L;
    public static final long BRONZE_PER_GOLD   = 10_000L;
    /** 1 vanilla emerald = 125 bronze. BUY must always be > SELL. */
    public static final long BRONZE_PER_EMERALD = 125L;

    public PokeWorldEconomy(IEventBus modBus) {
        modBus.addListener(this::setup);
        LOGGER.info("[PokeWorldEconomy] Mod loaded — CobbleDollars ⟺ Numismatic Overhaul integration active.");
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("[PokeWorldEconomy] Common setup complete.");
    }
}
