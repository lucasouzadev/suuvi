#!/usr/bin/env python3
"""Confere que todo glifo de design/emojis.json tem PNG no iOS e no Android e entrada no EmojiAssets.kt."""
import json, pathlib, sys
ROOT = pathlib.Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "scripts"))
from gen_emojis import name_of  # noqa: E402

glyphs = json.load(open(ROOT / "design/emojis.json", encoding="utf-8"))["glyphs"]
kt = (ROOT / "android/app/src/main/java/app/suuvi/design/EmojiAssets.kt").read_text(encoding="utf-8")
bad = []
for g in glyphs:
    n = name_of(g)
    for p in (ROOT / f"ios/Suuvi/Assets.xcassets/Emojis/{n}.imageset/{n}.png",
              ROOT / f"android/app/src/main/res/drawable-nodpi/{n}.png"):
        if not p.exists():
            bad.append(str(p))
    if f"R.drawable.{n}," not in kt:
        bad.append(f"kt:{n}")
print("emojis ok" if not bad else "faltando:\n" + "\n".join(bad))
sys.exit(1 if bad else 0)
