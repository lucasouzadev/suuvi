#!/usr/bin/env python3
"""Gera os icones do suuvi a partir de design/icons/*.svg (Lucide, ISC).

Uma fonte, dois formatos, mesmo desenho nas duas plataformas:
  - Android: android/app/src/main/res/drawable/ic_<nome>.xml (VectorDrawable)
  - iOS:     ios/Suuvi/Assets.xcassets/Icons/ic_<nome>.imageset (SVG, template)
Todos os icones sao tracos de 2px com pontas e cantos arredondados em grade 24x24.
"""
import json
import pathlib
import re
import shutil
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parent.parent
SRC = ROOT / "design" / "icons"
AND = ROOT / "android/app/src/main/res/drawable"
IOS = ROOT / "ios/Suuvi/Assets.xcassets/Icons"
NS = "{http://www.w3.org/2000/svg}"


def n(v: float) -> str:
    s = f"{v:.4f}".rstrip("0").rstrip(".")
    return s if s not in ("-0", "") else "0"


def f(el, key, default=0.0):
    return float(el.get(key, default))


def shape_to_d(el) -> str:
    tag = el.tag.replace(NS, "")
    if tag == "path":
        return el.get("d")
    if tag == "line":
        return f"M{n(f(el,'x1'))} {n(f(el,'y1'))}L{n(f(el,'x2'))} {n(f(el,'y2'))}"
    if tag in ("polyline", "polygon"):
        nums = [float(x) for x in re.split(r"[ ,]+", el.get("points").strip())]
        pts = list(zip(nums[0::2], nums[1::2]))
        d = "M" + "L".join(f"{n(x)} {n(y)}" for x, y in pts)
        return d + ("Z" if tag == "polygon" else "")
    if tag in ("circle", "ellipse"):
        cx, cy = f(el, "cx"), f(el, "cy")
        rx = f(el, "r") if tag == "circle" else f(el, "rx")
        ry = rx if tag == "circle" else f(el, "ry")
        return (f"M{n(cx - rx)} {n(cy)}a{n(rx)} {n(ry)} 0 1 0 {n(2 * rx)} 0"
                f"a{n(rx)} {n(ry)} 0 1 0 {n(-2 * rx)} 0Z")
    if tag == "rect":
        x, y, w, h = f(el, "x"), f(el, "y"), f(el, "width"), f(el, "height")
        rx = f(el, "rx", f(el, "ry", 0))
        ry = f(el, "ry", rx)
        if rx == 0 and ry == 0:
            return f"M{n(x)} {n(y)}H{n(x + w)}V{n(y + h)}H{n(x)}Z"
        return (f"M{n(x + rx)} {n(y)}H{n(x + w - rx)}a{n(rx)} {n(ry)} 0 0 1 {n(rx)} {n(ry)}"
                f"V{n(y + h - ry)}a{n(rx)} {n(ry)} 0 0 1 {n(-rx)} {n(ry)}"
                f"H{n(x + rx)}a{n(rx)} {n(ry)} 0 0 1 {n(-rx)} {n(-ry)}"
                f"V{n(y + ry)}a{n(rx)} {n(ry)} 0 0 1 {n(rx)} {n(-ry)}Z")
    raise SystemExit(f"forma nao suportada: {tag}")


def load(path: pathlib.Path) -> str:
    root = ET.fromstring(re.sub(r"<!--.*?-->", "", path.read_text(), flags=re.S))
    if root.get("viewBox") != "0 0 24 24" or root.get("stroke-width") != "2":
        raise SystemExit(f"{path.name}: esperado viewBox 24 e traco 2")
    return "".join(shape_to_d(el) for el in root.iter() if el is not root)


def main():
    for d in (AND, IOS):
        d.mkdir(parents=True, exist_ok=True)
    for old in AND.glob("ic_*.xml"):
        old.unlink()
    for old in IOS.glob("ic_*.imageset"):
        shutil.rmtree(old)
    (IOS.parent / "Contents.json").write_text(json.dumps({"info": {"author": "xcode", "version": 1}}, indent=2) + "\n")
    (IOS / "Contents.json").write_text(json.dumps({"info": {"author": "xcode", "version": 1}, "properties": {"provides-namespace": False}}, indent=2) + "\n")
    names = []
    for svg in sorted(SRC.glob("*.svg")):
        name = "ic_" + svg.stem.replace("-", "_")
        d = load(svg)
        names.append(name)
        (AND / f"{name}.xml").write_text(
            '<?xml version="1.0" encoding="utf-8"?>\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            '    android:width="24dp" android:height="24dp"\n'
            '    android:viewportWidth="24" android:viewportHeight="24">\n'
            '    <path\n'
            '        android:pathData="' + d + '"\n'
            '        android:strokeColor="#FF000000"\n'
            '        android:strokeWidth="2"\n'
            '        android:strokeLineCap="round"\n'
            '        android:strokeLineJoin="round" />\n'
            '</vector>\n')
        s = IOS / f"{name}.imageset"
        s.mkdir()
        (s / f"{name}.svg").write_text(
            '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">'
            f'<path d="{d}" fill="none" stroke="#000000" stroke-width="2" '
            'stroke-linecap="round" stroke-linejoin="round"/></svg>\n')
        (s / "Contents.json").write_text(json.dumps({
            "images": [{"filename": f"{name}.svg", "idiom": "universal"}],
            "info": {"author": "xcode", "version": 1},
            "properties": {"preserves-vector-representation": True, "template-rendering-intent": "template"},
        }, indent=2) + "\n")
    print("ok:", len(names), "icones ->", ", ".join(names))


if __name__ == "__main__":
    main()
