# HANDOFF — 2026-11-28 — suuvi v1 vertical slices

## Goal
Portfolio app (SwiftUI + Compose), local-first, parity via fixtures/*.json — approved by owner slice by slice (viewer from cutout, grid, lazy thumbs, shelf/folders/search from DB, smooth mesh, Edit shows saved image).

## State (verified, with evidence)
- Implementation: done through Edit hero photo — branch `main`, SHA `2d83ac7`
- Merge: pushed straight to main, no open PR
- Production: n/a (no store build; builds only on explicit owner order)
- QA: automated PASS — CI run 46100957403 (tokens+fixtures, ios, android tests+apk, android emulator), screenshots viewed.
- QA physical: PENDING — camera + Lift (Vision/ML Kit) cutout is NOT RUN in CI; 3D drag/pinch; Save tap with a real photo; Edit scroll on small Android; tap piece -> 3D; Search with new pieces; frame rate on device.

## Decisions (and why)
- Collection grid: slots(count)=max(4,count+count%2), lazy grid; thumbnails 480 px cached.
- Mesh: Chaikin x2 + Delaunay + bevel, fixed-point, same fixtures on both platforms; coin fallback on 64 grid.
- Edit: with image, photo is the front hero card, emoji cards behind, fields below; without image, old cascade.
- Shelf/Folders order by effective last-use day (saved pieces count).

## Files touched (last slice)
- ios/Suuvi/Components/EditParts.swift, Screens/EditView.swift — HeroStack + layout
- android/.../ui/components/EditParts.kt, ui/EditScreen.kt — same
- .github/workflows/ci.yml — edit-photo screenshots

## Next step (exact, first action)
Owner picks the next item (see list below); then plan + approval before any edit.
Candidates: (1) note field so Shelf "suvenir do dia" can use saved pieces; (2) photo texture on the 3D front face; (3) tap piece in Collection -> Edit/delete/rename saved pieces; (4) toggle low-poly vs smooth; (5) physical test round of camera + Lift.

## Risks / blocked / owner decisions needed
- Lift never verified on a real device or in CI.
- Owner has not decided on the note field.
