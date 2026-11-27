#!/usr/bin/env python3
"""design/emojis.json + Fluent Emoji (Flat, MIT) -> PNGs iguais no iOS e no Android.

Uso: python3 scripts/gen_emojis.py <pasta clonada de microsoft/fluentui-emoji>
Requer rsvg-convert. As imagens geradas sao versionadas (o CI nao regera, so confere com check_emojis.py).
Nome do arquivo: emoji_<codepoints em hex, sem U+FE0F, separados por _>.
"""
import glob, json, os, pathlib, shutil, subprocess, sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
PX = 192  # cobre 44pt @ 3x com folga


def name_of(glyph: str) -> str:
    return "emoji_" + "_".join(f"{ord(c):x}" for c in glyph if c != "️")


def main(src: str) -> None:
    glyphs = json.load(open(ROOT / "design/emojis.json", encoding="utf-8"))["glyphs"]
    idx = {}
    for m in glob.glob(f"{src}/assets/*/metadata.json"):
        g = json.load(open(m, encoding="utf-8")).get("glyph", "").replace("️", "")
        flat = glob.glob(os.path.dirname(m) + "/Flat/*.svg")
        if flat:
            idx[g] = flat[0]
    ios = ROOT / "ios/Suuvi/Assets.xcassets/Emojis"
    droid = ROOT / "android/app/src/main/res/drawable-nodpi"
    for d in (ios,):
        shutil.rmtree(d, ignore_errors=True)
    for old in droid.glob("emoji_*.png"):
        old.unlink()
    ios.mkdir(parents=True)
    (ios / "Contents.json").write_text('{ "info" : { "author" : "xcode", "version" : 1 }, "properties" : { "provides-namespace" : false } }\n')
    droid.mkdir(parents=True, exist_ok=True)
    entries = []
    for g in glyphs:
        key = g.replace("️", "")
        svg = idx[key]  # KeyError = glifo sem arte: corrija a lista
        n = name_of(g)
        subprocess.run(["rsvg-convert", "-w", str(PX), "-h", str(PX), svg, "-o", f"/tmp/{n}.png"], check=True)
        d = ios / f"{n}.imageset"
        d.mkdir()
        shutil.copy(f"/tmp/{n}.png", d / f"{n}.png")
        (d / "Contents.json").write_text(json.dumps({
            "images": [{"idiom": "universal", "filename": f"{n}.png"}],
            "info": {"author": "xcode", "version": 1}}, indent=2) + "\n")
        shutil.copy(f"/tmp/{n}.png", droid / f"{n}.png")
        entries.append((g.replace("️", ""), n))
    kt = ROOT / "android/app/src/main/java/app/suuvi/design/EmojiAssets.kt"
    lines = ["// GERADO por scripts/gen_emojis.py. Nao edite.", "package app.suuvi.design", "", "import app.suuvi.R", "",
             "object EmojiAssets {", "    private val map: Map<String, Int> = mapOf("]
    lines += [f'        "{g}" to R.drawable.{n},' for g, n in entries]
    lines += ["    )", "", "    /** Recurso do emoji (sem seletor de variacao) ou null se nao ha arte. */",
              '    fun resFor(glyph: String): Int? = map[glyph.replace("\\uFE0F", "")]', "}", ""]
    kt.write_text("\n".join(lines), encoding="utf-8")
    print(f"ok: {len(entries)} emojis")


if __name__ == "__main__":
    main(sys.argv[1])
