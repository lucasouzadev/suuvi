#!/usr/bin/env python3
"""Gera Tokens.swift e Tokens.kt a partir de design/tokens.json. So stdlib."""
import json
import pathlib

ROOT = pathlib.Path(__file__).resolve().parent.parent
tokens = json.loads((ROOT / "design/tokens.json").read_text(encoding="utf-8"))


def camel(key):
    parts = key.split("-")
    return parts[0] + "".join(p.capitalize() for p in parts[1:])


def hex6(value):
    return value.lstrip("#").upper()


HEADER = "// GERADO por scripts/gen_tokens.py a partir de design/tokens.json. Nao editar.\n"

swift = [HEADER, "import SwiftUI\n", "enum SuuviColor {"]
for k, v in tokens["color"].items():
    swift.append(f"    static let {camel(k)} = Color(hex: 0x{hex6(v)})")
swift.append("}\n")
swift.append("enum SuuviSpace {")
for i, v in enumerate(tokens["space"]):
    swift.append(f"    static let s{i} = CGFloat({v})")
swift.append("}\n")
swift.append("enum SuuviRadius {")
for k, v in tokens["radius"].items():
    swift.append(f"    static let {camel(k)}: CGFloat = {v}")
swift.append("}")
(ROOT / "ios/Suuvi/Design/Tokens.swift").write_text("\n".join(swift) + "\n", encoding="utf-8")

kt = [HEADER, "package app.suuvi.design\n", "import androidx.compose.ui.graphics.Color",
      "import androidx.compose.ui.unit.dp\n", "object SuuviColor {"]
for k, v in tokens["color"].items():
    kt.append(f"    val {camel(k)} = Color(0xFF{hex6(v)})")
kt.append("}\n")
kt.append("object SuuviSpace {")
for i, v in enumerate(tokens["space"]):
    kt.append(f"    val s{i} = {v}.dp")
kt.append("}\n")
kt.append("object SuuviRadius {")
for k, v in tokens["radius"].items():
    kt.append(f"    val {camel(k)} = {v}.dp")
kt.append("}")
(ROOT / "android/app/src/main/java/app/suuvi/design/Tokens.kt").write_text("\n".join(kt) + "\n", encoding="utf-8")
print("ok: Tokens.swift, Tokens.kt")
