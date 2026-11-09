package pokeworld.economy.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import notsosig.numismaticoverhaul.attachment.CurrencyHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pokeworld.economy.PokeWorldEconomy;

/**
 * Mixin into MCA's VillagerCommandHandler to accept NO Purse for hiring.
 *
 * MCA payEmeralds(ServerPlayer, int) scans the inventory for Items.EMERALD.
 * We redirect it to also draw from the player's CurrencyHolder (Purse).
 *
 * Rate: 1 emerald = 125 bronze = 125 CobbleDollars.
 *
 * Target: net.mca.server.command.VillagerCommandHandler
 */
@Mixin(targets = "net.mca.server.command.VillagerCommandHandler", remap = false)
public abstract class MCAVillagerCommandMixin {

    /**
     * Inject at the head of payEmeralds.
     * If the player has enough in Purse, deduct and cancel inventory scan.
     *
     * hire_short = 5 emeralds = 625 bronze
     * hire_long  = 10 emeralds = 1250 bronze
     */
    @Inject(method = "payEmeralds", at = @At("HEAD"), cancellable = true)
    private static void pokeworld$payEmeralds(ServerPlayer player, int emeraldCount,
                                               CallbackInfoReturnable<Boolean> cir) {
        long cost = (long) emeraldCount * PokeWorldEconomy.BRONZE_PER_EMERALD;
        CurrencyHolder holder = CurrencyHolder.get(player);
        if (holder != null && holder.getBalance() >= cost) {
            holder.modify(-cost);
            cir.setReturnValue(true);
        }
        // If not enough in purse, fall through to vanilla inventory check
    }
}
