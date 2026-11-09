package pokeworld.economy.mixin;

import net.minecraft.server.level.ServerPlayer;
import notsosig.numismaticoverhaul.attachment.CurrencyHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pokeworld.economy.PokeWorldEconomy;

/**
 * Mixin into MCA's GiftPredicate so "emeralds" gift condition
 * checks the NO Purse balance instead of (or in addition to) inventory.
 *
 * GiftPredicate lambda$static$56 / lambda$static$55 checks:
 *   player.getInventory().countItem(Items.EMERALD) >= count
 *
 * We redirect so it also passes when the Purse holds >= count * 125 bronze.
 *
 * Target: net.mca.entity.interaction.gifts.GiftPredicate
 */
@Mixin(targets = "net.mca.entity.interaction.gifts.GiftPredicate", remap = false)
public abstract class MCAGiftPredicateMixin {

    /**
     * Lambda target — the actual inner lambda checking emerald count.
     * Method name verified against mca_gifts_emeralds.txt javap output.
     *
     * Inject AFTER original check; if original returned false, check Purse.
     */
    @Inject(
        method = "lambda$static$56(Lnet/minecraft/server/level/ServerPlayer;I)Z",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void pokeworld$emeraldGiftPurseCheck(ServerPlayer player, int count,
                                                          CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            long required = (long) count * PokeWorldEconomy.BRONZE_PER_EMERALD;
            CurrencyHolder holder = CurrencyHolder.get(player);
            if (holder != null && holder.getBalance() >= required) {
                cir.setReturnValue(true);
            }
        }
    }
}
