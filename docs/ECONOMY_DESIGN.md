# PokeWorld Economy — Дизайн экономики

## Курс валют

```
1 bronze_coin  == 1 CobbleDollar
1 silver_coin  == 100 bronze
1 gold_coin    == 10 000 bronze
1 emerald      == 125 bronze  (BUY > SELL везде!)
```

## Правило anti-exploit

Для КАЖДОГО предмета должно быть строго:
```
цена_продажи (bank) < цена_покупки (shop)
```

Дополнительно учитываем Fortune III (+2 drop) и крафт-цепочки:
- `gold_nugget × 9 → gold_ingot × 1` (stonecutter/craft)
- `gold_ingot × 9 → gold_block × 1`
- `gold_ore + Fortune III → 2–4 gold_ingot`

## Таблица исправлений CCS

| Файл | Предмет | Было | Стало | Причина |
|------|---------|------|-------|---------|
| `config/cobbledollars/default_bank.json` | minecraft:emerald | 750 sell | 100 sell | курс 125, нет эксплойта |
| `CCS/bank/pokemart.json` | cobblemon:technical_machine | 10 000 sell | УДАЛЕНО | покупается за 500 → ×20 |
| `CCS/shop/ore.json` | minecraft:gold_ore | 4500 buy | 10 000 buy | Fortune III даёт ≥2 ingot×4500=9000 |
| `CCS/bank/pokemart.json` | minecraft:gold_ingot | 4500 sell | 4000 sell | ниже shop ore / Fortune cap |

## Алгоритм CurrencyHelper.getClosest fix

```
function getClosest(value: Long): ItemStack[] {
    gold   = value / 10000
    rem    = value % 10000
    silver = rem / 100
    bronze = rem % 100

    result = []
    // stack limit = 99
    if gold > 0:   result += stacks(gold_coin,   gold,   99)
    if silver > 0: result += stacks(silver_coin, silver, 99)
    if bronze > 0: result += stacks(bronze_coin, bronze, 99)
    return result  // may be multiple slots; use costA + costB for trade
}
```

## MCA интеграция

### Hire (найм)
- `hire_short` = 5 emeralds = **625 bronze** из Purse
- `hire_long`  = 10 emeralds = **1 250 bronze** из Purse
- Сначала проверяем Purse, если нет — inventory (vanilla fallback)

### Taxes
```json
// config/mca.json
"taxesMap": {
  "numismaticoverhaul:silver_coin": 0.8
}
```
Деления/умножения в `VillageTaxesManager.taxes()` — проверить что factor=0.8 корректен.

### Gifts
GiftPredicate `"emeralds"` — проверяет `Purse >= count * 125` ИЛИ inventory.

## Mixin цели (проверить javap перед компиляцией)

| Mixin | Target class | Method |
|-------|-------------|--------|
| BankMixin | `net.genki.cobbledollars.bank.Bank` | `contains(ItemStack)Z`, `getPrice(ItemStack)J` |
| MCAVillagerCommandMixin | `net.mca.server.command.VillagerCommandHandler` | `payEmeralds(ServerPlayer, int)Z` |
| MCAGiftPredicateMixin | `net.mca.entity.interaction.gifts.GiftPredicate` | lambda `$56` |
| NOCurrencyHelperMixin | `notsosig.numismaticoverhaul.currency.CurrencyHelper` | `getClosest(J)[LItemStack;` |
| NOVillagerTradesRegistryMixin | `notsosig.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry` | `wrapModVillagers()V`, `getRegistryForLoading()` |
