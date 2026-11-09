# Аудит арбитража — PokeWorld Economy

## Статус: 🔴 Найдено / 🟢 Исправлено

### Прямые арбитраж-петли (BUY ≤ SELL)

| # | Предмет | BUY (shop) | SELL (bank) | Множитель | Статус |
|---|---------|-----------|-------------|-----------|--------|
| 1 | `cobblemon:technical_machine` | 500 (CCS/shop/tm.json) | 10 000 (CCS/bank/pokemart.json) | ×20 | 🟢 TM удалён из банка |
| 2 | `minecraft:emerald` | — | 750 (config/default_bank.json) | ×6 от курса 125 | 🟢 Снижено до 100 |

### Fortune III / crafting loops

| # | Цепочка | Profit | Статус |
|---|---------|--------|--------|
| 1 | `gold_ore` (buy 4500) + Fortune III → 2–4 `gold_ingot` (sell 4500×2=9000) | +4500–13500 | 🟢 ore: 10000, ingot sell: 4000 |
| 2 | `gold_ingot×9` → `gold_block` craft → sell 40500, купить 9×4000=36000 | +4500 | 🟢 block sell: 36000 = 9×4000 |
| 3 | `gold_nugget×9` → `gold_ingot` → sell 4000, купить 9×500=4500 | -500 (OK) | ✅ |

### Соотношение buy/sell для CCS магазинов (должно быть buy > sell)

| Предмет | BUY (shop) | SELL (bank) | Соотношение | Статус |
|---------|-----------|-------------|-------------|--------|
| medicinal_leek | 100 | 50 | 2× | ✅ |
| relic_coin | 1000 | 500 | 2× | ✅ |
| wheat | 100 | 50 | 2× | ✅ |
| carrot/potato/beetroot | 100 | 50 | 2× | ✅ |
| sugar_cane | 100 | 50 | 2× | ✅ |
| melon | — | 200 | только продажа | ✅ |
| pumpkin | — | 200 | только продажа | ✅ |

### Numismatic Villager Trades — проверить

- [ ] `toolsmith.json` цены — проверить нет ли `buy <= sell`
- [ ] `wandering_trader.json` — должны reload-persist корректно
- [ ] RCTMod TrainerAssociation 1–8 изумрудов → конвертировать

### Bountiful + Numismatic-Bounties

- [ ] Проверить `_all_objs.json` — стоимость заданий в emeralds
- [ ] Проверить `_all_rews.json` — награды в монетах  
- [ ] DecreeTradeFactory — 2^(n-1)+1 изумрудов → конвертировать

## Статус проверки модов

| Мод | Арбитраж | Emerald refs | Статус |
|-----|---------|-------------|--------|
| CobbleDollars (config) | ✅ исправлен | 750→100 | 🟢 |
| CCS bank/shop | ✅ исправлен | TM/gold фикс | 🟢 |
| Numismatic Overhaul | ⚠️ 64-stack баг | — | 🔴 Mixin нужен |
| MCA | — | hire/gifts/taxes | 🔴 Mixin нужен |
| RCTMod | — | 1-8 emeralds | 🔴 TODO |
| Bountiful | — | объекты/награды | 🔴 TODO |
| LegendaryMonuments | — | loot tables | 🟢 уже в датапаке |
