#!/usr/bin/env python3
"""
Verify economy exploits after deploy.
Run before and after to compare.
"""

import zipfile
import json
from pathlib import Path

MINECRAFT = Path("/home/falafel/.local/share/PrismLauncher/instances/PokeWorld/minecraft")
CCS_ZIP = MINECRAFT / "config/paxi/datapacks/Cobblemon Cities & Structures-1.0.16.zip"
FIXES_ZIP = MINECRAFT / "config/paxi/datapacks/pokeworld_fixes.zip"
CD_CONFIG = MINECRAFT / "config/cobbledollars/default_bank.json"

ISSUES = []

def check(condition: bool, msg: str, severity="ERROR"):
    if not condition:
        ISSUES.append(f"[{severity}] {msg}")
        print(f"  ❌ {msg}")
    else:
        print(f"  ✅ {msg}")

def read_zip_json(zp: Path, entry: str):
    with zipfile.ZipFile(zp) as z:
        return json.loads(z.read(entry))

def main():
    print("=== Economy Exploit Audit ===\n")

    # 1. default_bank.json emerald price
    print("[1] default_bank.json emerald price...")
    bank = json.loads(CD_CONFIG.read_text())
    em = next((x for x in bank if x.get("item") == "minecraft:emerald"), None)
    if em:
        price = int(em["price"])
        check(price <= 100, f"emerald sell price {price} <= 100 (no 125 exploit)", "ERROR")
    else:
        print("  ⚠️  emerald not in default_bank.json (OK if removed)")

    # 2. TM in CCS bank
    print("\n[2] CCS bank/pokemart.json — TM must NOT be there...")
    try:
        ccs_bank = read_zip_json(CCS_ZIP, "data/ccs/cobbledollars/bank/pokemart.json")
        tm_entries = [x for x in ccs_bank if x.get("item") == "cobblemon:technical_machine"]
        check(len(tm_entries) == 0, "TM not in CCS bank (would be overridden by fixes)", "WARNING")
    except Exception as e:
        print(f"  ⚠️ Could not read CCS zip: {e}")

    # 3. Check fixes zip overrides TM
    print("\n[3] pokeworld_fixes.zip — bank/pokemart.json without TM...")
    try:
        fixes_bank = read_zip_json(FIXES_ZIP, "data/ccs/cobbledollars/bank/pokemart.json")
        tm_in_fixes = [x for x in fixes_bank if x.get("item") == "cobblemon:technical_machine"]
        check(len(tm_in_fixes) == 0, "TM removed from fixes bank/pokemart.json", "ERROR")
    except KeyError:
        print("  ℹ️  fixes zip doesn't override CCS bank/pokemart yet — NEEDED!")
        ISSUES.append("[ERROR] pokeworld_fixes must override CCS bank/pokemart.json without TM")

    # 4. Gold arbitrage
    print("\n[4] Gold ingot sell price <= gold ore buy price / 2...")
    try:
        ore_shop = read_zip_json(CCS_ZIP, "data/ccs/cobbledollars/shop/ore.json")
        gold_ore_buy = next(
            (int(o["price"]) for s in ore_shop for o in s.get("offers", [])
             if o.get("item") == "minecraft:gold_ore"), None)
        bank_data = read_zip_json(CCS_ZIP, "data/ccs/cobbledollars/bank/pokemart.json")
        gold_ingot_sell = next((int(x["price"]) for x in bank_data
                                if x.get("item") == "minecraft:gold_ingot"), None)
        if gold_ore_buy and gold_ingot_sell:
            # Fortune III can give 4 ingots per ore
            max_profit = gold_ingot_sell * 4 - gold_ore_buy
            check(max_profit <= 0,
                  f"gold_ore buy {gold_ore_buy}, ingot sell {gold_ingot_sell}, Fortune III profit: {max_profit}",
                  "ERROR")
        else:
            print("  ℹ️  Could not find gold prices in CCS (may be overridden)")
    except Exception as e:
        print(f"  ⚠️ Error: {e}")

    # Summary
    print(f"\n{'='*40}")
    if ISSUES:
        print(f"❌ {len(ISSUES)} issue(s) found:")
        for i in ISSUES:
            print(f"   {i}")
    else:
        print("✅ No exploits found!")

if __name__ == "__main__":
    main()
