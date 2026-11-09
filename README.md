# PokeWorld Economy Integration

> **MC 1.21.1 · NeoForge 21.1.259 · JDK 21**

Полная интеграция **CobbleDollars** + **Numismatic Overhaul** в единую экономику для сборки PokeWorld.

## Конвертация валюты

| Единица | Значение в бронзе |
|---------|-----------------|
| 1 bronze coin | 1 |
| 1 silver coin | 100 |
| 1 gold coin | 10 000 |
| 1 emerald (vanilla) | 125 bronze |
| 1 CobbleDollar | 1 bronze coin |

## Структура проекта

```
pokeworld-economy/
├── src/                        # Java-исходники мода pokeworld_economy
│   └── main/
│       ├── java/pokeworld/economy/
│       │   ├── mixin/          # Mixins: MCA, CD, NO, RCTMod
│       │   ├── loot/           # Global Loot Modifier (emerald → coins)
│       │   └── PokeWorldEconomy.java
│       └── resources/
│           ├── META-INF/       # mods.toml, MANIFEST.MF
│           ├── mixins.json
│           └── data/pokeworld_economy/
├── datapacks/
│   └── pokeworld_fixes/        # Патч-датапак (оверрайды банка/магазина/лута)
│       └── data/
│           ├── cobbledollars/  # Правки bank/shop CCS
│           ├── numismaticoverhaul/villager_trades/
│           ├── bountiful/      # Numismatic-Bounties пулы
│           └── legendarymonuments/loot_table/
├── resourcepacks/
│   └── pokeworld_fixes_rp/     # lang/ru_ru.json патчи
│       └── assets/
│           ├── cobbledollars/lang/
│           ├── numismaticoverhaul/lang/
│           └── cobblemon/lang/
├── configs/                    # Готовые конфиги (копировать в minecraft/config/)
│   ├── cobbledollars/
│   │   ├── default_bank.json   # emerald: 750 → 100
│   │   └── default_shop.json
│   ├── mca.json                # taxesMap: silver_coin
│   ├── numismaticoverhaul-common.toml
│   └── numismaticoverhaul-client.toml
├── build.gradle
├── gradle.properties
└── docs/
    ├── ECONOMY_DESIGN.md       # Описание экономики, курсы, exploit-защита
    └── AUDIT.md                # Результаты аудита арбитража
```

## Ключевые фиксы

### 🔴 Критические арбитраж-эксплойты (исправлено)
- `CCS/shop/tm.json`: TM покупается за 500 → продаётся за 10 000 (**×20 бесконечная петля**)
- `config/cobbledollars/default_bank.json`: emerald = 750 при курсе 125 (**×6 эксплойт**)
- `CCS/shop/ore.json`: gold_ore = 4500 → gold_ingot = 4500 + Fortune III (**бесконечное золото**)

### 🟡 Баги NO Villager Trades (исправлено)
- `wrapModVillagers` пропускает все профессии из `TRADES_REGISTRY` → модовые трейды TravelersBackpack/Cobblemon теряются
- `/reload` стирает `REMAPPED_FABRIC_WANDERING_TRADER_TRADES` → странники теряют трейды
- `CurrencyHelper.getClosest` обрезает стеки монет на 64 вместо 99 → неправильные цены

### 🟢 Интеграции
- **MCA Hire**: `payEmeralds` → принимает NO Purse + физические монеты
- **MCA Taxes**: `taxesMap` → `numismaticoverhaul:silver_coin`
- **MCA Gifts**: `GiftPredicate "emeralds"` → считает монеты в кошельке
- **RCTMod TrainerAssociation**: 1–8 изумрудов → эквивалент монетами
- **Bountiful + Numismatic-Bounties**: проверка exploit-free пулов

## Сборка

```bash
./gradlew build
# Скопировать mods/pokeworld_economy-1.0.0.jar → minecraft/mods/
# Скопировать datapacks/pokeworld_fixes/ → config/paxi/datapacks/pokeworld_fixes.zip
# Скопировать resourcepacks/pokeworld_fixes_rp/ → config/paxi/resourcepacks/pokeworld_fixes_rp.zip
```

## Требования

- NeoForge `21.1.259` для MC `1.21.1`
- CobbleDollars `2.0.0+Beta-6.1`
- Numismatic Overhaul `4.0.0`
- MCA `7.7.36-beta.3`
