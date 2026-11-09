#!/usr/bin/env python3
"""
Build script: package datapacks and resourcepacks into ZIP files
and copy them to the Minecraft instance.

Usage:
    python3 scripts/deploy.py

Requires:
    - Running from pokeworld-economy/ root
    - MINECRAFT_DIR set or default path used
"""

import os
import shutil
import zipfile
import hashlib
from pathlib import Path

MINECRAFT = Path(os.environ.get(
    "MINECRAFT_DIR",
    "/home/falafel/.local/share/PrismLauncher/instances/PokeWorld/minecraft"
))

REPO = Path(__file__).parent.parent
DATAPACKS_SRC = REPO / "datapacks" / "pokeworld_fixes"
RESOURCEPACKS_SRC = REPO / "resourcepacks" / "pokeworld_fixes_rp"
CONFIGS_SRC = REPO / "configs"

PAXI_DATAPACKS = MINECRAFT / "config" / "paxi" / "datapacks"
PAXI_RESOURCEPACKS = MINECRAFT / "config" / "paxi" / "resourcepacks"
RESOURCEPACKS_DIR = MINECRAFT / "resourcepacks"
CONFIG_DIR = MINECRAFT / "config"


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    h.update(path.read_bytes())
    return h.hexdigest()


def merge_zip(src_dir: Path, existing_zip: Path, output_zip: Path):
    """Merge src_dir files into existing_zip, overwriting matching entries."""
    tmp = output_zip.with_suffix(".tmp.zip")

    # Read all entries from existing zip
    existing_entries = {}
    if existing_zip.exists():
        with zipfile.ZipFile(existing_zip, 'r') as z:
            for name in z.namelist():
                existing_entries[name] = z.read(name)

    # Collect new entries from src_dir
    new_entries = {}
    for f in src_dir.rglob("*"):
        if f.is_file():
            rel = f.relative_to(src_dir)
            new_entries[str(rel).replace("\\", "/")] = f.read_bytes()

    # Merge: new entries override existing
    merged = {**existing_entries, **new_entries}

    with zipfile.ZipFile(tmp, 'w', compression=zipfile.ZIP_DEFLATED) as zout:
        for name, data in merged.items():
            zout.writestr(name, data)

    tmp.replace(output_zip)
    print(f"  Merged {len(new_entries)} new entries into {output_zip.name} ({len(merged)} total)")
    return len(merged)


def deploy_configs():
    """Copy config files to minecraft/config/"""
    for f in CONFIGS_SRC.rglob("*"):
        if f.is_file():
            rel = f.relative_to(CONFIGS_SRC)
            dst = CONFIG_DIR / rel
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(f, dst)
            print(f"  Config: {rel}")


def main():
    print("=== PokeWorld Economy Deploy ===\n")
    print(f"Minecraft: {MINECRAFT}")

    # 1. Datapack
    print("\n[1] Patching pokeworld_fixes.zip ...")
    datapack_zip = PAXI_DATAPACKS / "pokeworld_fixes.zip"
    count = merge_zip(DATAPACKS_SRC, datapack_zip, datapack_zip)
    assert count >= 11, f"Expected >=11 entries, got {count}"
    print(f"  ✅ {count} entries, OK")

    # 2. ResourcePack (paxi)
    print("\n[2] Patching pokeworld_fixes_rp.zip (paxi) ...")
    rp_paxi = PAXI_RESOURCEPACKS / "pokeworld_fixes_rp.zip"
    count_rp = merge_zip(RESOURCEPACKS_SRC, rp_paxi, rp_paxi)
    assert count_rp >= 4, f"Expected >=4 entries, got {count_rp}"
    print(f"  ✅ {count_rp} entries, OK")

    # 3. ResourcePack (resourcepacks/ — byte-identical copy)
    print("\n[3] Syncing to resourcepacks/ directory ...")
    rp_main = RESOURCEPACKS_DIR / "pokeworld_fixes_rp.zip"
    shutil.copy2(rp_paxi, rp_main)
    h1 = sha256(rp_paxi)
    h2 = sha256(rp_main)
    assert h1 == h2, "SHA256 mismatch between paxi and resourcepacks!"
    print(f"  ✅ SHA256 match: {h1[:16]}...")

    # 4. Configs
    print("\n[4] Deploying configs ...")
    deploy_configs()
    print("  ✅ Configs deployed")

    print("\n=== Deploy complete! ===")
    print("Restart Minecraft to apply changes.")


if __name__ == "__main__":
    main()
