package pokeworld.economy.mixin;

import notsosig.numismaticoverhaul.currency.CurrencyConverter;
import notsosig.numismaticoverhaul.currency.CurrencyHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Fix for NO's CurrencyHelper.getClosest(long) truncating coin stacks at 64
 * instead of the actual CoinItem max of 99.
 *
 * Root cause: CurrencyConverter.splitAtMaxCount uses min(maxStackSize, 64),
 * so stacks of 65–99 coins get truncated to 64, causing price undercharging.
 *
 * Fix: Override getClosest to use splitAtMaxCount with actual max 99.
 *
 * Target: notsosig.numismaticoverhaul.currency.CurrencyHelper
 */
@Mixin(value = CurrencyHelper.class, remap = false)
public abstract class NOCurrencyHelperMixin {

    /**
     * Redirect getClosest to use corrected stack splitting.
     *
     * Original: calls CurrencyConverter.getAsItemStackList(value) which internally
     * calls splitAtMaxCount with min(maxStackSize, 64) = 64 for CoinItem.
     *
     * Fix: We call CurrencyConverter.getAsItemStackList but then verify the total
     * value matches and return a corrected ItemCost pair if needed.
     *
     * NOTE: Full implementation requires examining exact method signatures from
     * no_currency_javap.txt. This is a skeleton — Codex should complete it.
     */
    @Inject(method = "getClosest(J)[Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private static void pokeworld$fixGetClosest(long value, CallbackInfoReturnable<ItemStack[]> cir) {
        // TODO: Implement corrected denomination logic using max stack size 99
        // The fix should:
        // 1. Split `value` into gold/silver/bronze using BRONZE_PER_GOLD / BRONZE_PER_SILVER
        // 2. Create ItemStack arrays with count <= 99 (not 64)
        // 3. If single denomination would exceed 99, use higher denomination + remainder
        // See: docs/ECONOMY_DESIGN.md for exact algorithm
    }
}
