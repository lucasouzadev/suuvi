# HANDOFF — 2026-11-28 — suuvi roadmap items 1–4

## Goal
Roadmap ordered by owner, done in order — approved 2026-11-28: (1) edit/rename/delete saved pieces, (2) note field so the Shelf daily souvenir uses saved pieces, (3) photo texture on the 3D front face, (4) low-poly vs smooth toggle.

## State (verified, with evidence)
- Implementation: items 1–4 done on main — last SHA `6387e77` (toggle). Earlier: cb02a8c (1), cebdd60 (2), a379ab0 (3).
- Merge: pushed straight to origin/main. Production: n/a (local-first app, no deploy).
- QA automated: PASS — CI run 46122778302, 4 jobs green (tokens+fixtures, android unit+apk, ios simulator, android emulator). Screenshots viewed: edit-piece, delete-ask, deleted, shelf-note, viewer-photo, viewer-cutout, viewer-low.
- QA physical: pending (see below).

## Decisions (and why)
- Long-press menu (Edit/Delete) on saved cards, confirmation dialog on delete; piece numbers are never reused (per-folder counter persisted).
- Note: 4th optional field, max 140 chars; daily pick = any saved piece, one per day, mixed with samples via ShelfRules.dailyOrder.
- 3D: photo is the front-face texture via one affine (orthographic); sides/bevel keep accent color.
- Toggle: header button in the Viewer (Lucide box/circle), choice persisted (`viewerSmooth`), low-poly keeps the front photo.
- Parity: every rule validated by shared fixtures (create, shelf, shape, viewer).

## Files touched
- fixtures/create.json, shelf.json, shape.json, viewer.json — shared expectations
- PieceRules / EditRules / ShelfRules / ShapeRules / MeshRules / ViewerRules (Swift + Kotlin) — rules
- PieceStore, ImageStore, ShapeLoader, SuuviDatabase v4 (note column + MIGRATION_3_4)
- Collection/Edit/Shelf/Viewer screens, DailyCard, strings en + pt-BR, design/icons/box.svg
- .github/workflows/ci.yml — debug routes viewerphoto, viewerlow, editpiece, deleteask, deleted, shelfnote

## Next step (exact, first action)
Owner runs the PHYSICAL list on a real device; then decide next roadmap items.

## Risks / blocked / owner decisions needed
- NOT PROVEN (CI cannot): long-press gesture and Delete tap, counter non-reuse and file removal on device, Vision/ML Kit lift and camera, real cutout looking right as texture, drag/pinch and frame rate in 3D, Edit scroll on small Android, note truncation (single line), toggle tap persistence across launches.
- Known cosmetic: slight jagged slivers near the heart notch (earlier mesh work).

## Physical test list
- Create a piece with photo and note; long-press it: Edit prefill (incl. note), save; Delete + confirm; create another and check the number is not reused.
- Open the Viewer on a real lifted piece: texture alignment, toggle smooth/low-poly, relaunch and check the choice stuck.
- Shelf: with a noted saved piece, daily card shows it.
