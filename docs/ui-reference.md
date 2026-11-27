# UI reference notes (from owner screenshots, 2026-11-27)

Structure and components only. Reference images, names, handles and texts are NOT reused (project invariant).
Values are visual estimates. Animations, transitions and keyboard states were not visible.

## Flow (owner order) -> suuvi screen
| # | Owner screen | Reference | suuvi |
|---|---|---|---|
| 1 | Home | Inbox | **Shelf** |
| 2 | Settings (gear) | none provided | native grouped list (decided) |
| 3 | Folders (folder icon) | Folders grid | **Folders** |
| 4 | Search + filter (magnifier) | Dictionary | **Search** |
| 5 | Collection | round with tilted cards (WIP) | **Collection** |
| 6 | Item edit | cascade cards + inputs | **Edit** |
| 7 | Edit done + check | filled inputs + answers | **Edit (done)** |

## 1 Home (Shelf)
- Header: gear (left), centered title, folder icon + thin divider + search icon (right).
- Daily card: border 1px light grey, radius ~16, bold title ("Today: emoji name"), 3-line body, footer = calendar icon + date (muted, left) and source mark (right).
- Segmented pill: grey track, selected segment white with hairline border. suuvi labels: **Colecao / Recebidas**.
- Row: drag bar `|` (grey), chip (letter + emoji, bordered, rounded), `@place` bold, small grey type letter, `#` + number, thin vertical divider, status icon (amber check = ready, circle = draft, cross = failed). Hairline separators.
- Empty/helper text: centered, line 1 bold dark, following lines muted grey.
- Footer: version with chevron (left), **Create** pill (center, bordered), icon (right).

## 3 Folders
- Back chevron (left), centered title. Grid 2 columns, card radius ~16 with 1px light border, emoji centered, `@place` bottom-left (muted), `#n` bottom-right (muted, larger). Same footer (version plain, Create, icon).

## 4 Search + filter
- Centered title, close X in grey circle (right). Chips: 3 columns, outlined pills, bold words. Results: 2-column masonry, rounded image cards, caption left, "Source" muted right. No search field in the reference: suuvi adds a native search field.
- Content scrolls under the home indicator in the reference: suuvi respects safe area.

## 5 Collection
- Back chevron, chip centered; line "from X" (bold, left) + `@x` + `#n` + status (right).
- Grid 2x2 of cards, each slightly rotated; empty slots dashed with "+".
- Caption below grid ("(WIP)" style). Footer: timer icon + text (left), action pill (center), info icon (right).
- Profile sheet (modal): close X in grey circle, big emoji, name (+ badge), subtitle, meta row (icon + name, calendar + start date), segmented pill + share icon, rows with chevron on the left.

## 6 Edit
- Cards overlap in a vertical cascade, each slightly tilted; fields on the right: pill inputs with numbered circle (1,2,3), active field has caret, others show muted hint letters. Action pill greyed until valid.

## 7 Edit done
- Filled pills, a muted check + value under each, centered block (title + description + hint), action pill changes label, amber check at the top row.

## suuvi adaptation
- Cards = souvenir pieces (cutout / 3D thumbnail); dashed slot = add piece; inputs = title, place, tag; timer text -> piece count or date; action pill = save, label changes after save.

## Findings (media-investigation)
- Reference footer inconsistent (version 2.3.7 chevron vs 2.3.4 plain; "24h left" vs "24H LEFT"): suuvi standardizes.
- Small muted letters, circle and cross icons look below 4.5:1 (INFERRED): use `text-muted` #6B6B6B and accent-ink for the check.
- Status icons and drag bar look under 44pt touch target (INFERRED): enlarge hit area.

## Icons (decided 2026-11-27)
- Set: Lucide (ISC), rounded 2px stroke on a 24 grid, same drawing on both platforms. Sources in `design/icons/*.svg`; `scripts/gen_icons.py` generates Android VectorDrawables and iOS asset-catalog SVGs. No SF Symbols and no Material icons in the UI.

## Search + filter (implemented)
- Sheet/screen: title "Search" centered, X in grey circle (32pt visual, 44/48 touch) top right, pill search field, 3-column outlined tag chips (selected = amber), 2-column masonry cards (emoji block with varying height + name and @place caption).
- Rules (parity): `fixtures/search.json` — query matches name, place or tag; selected tags are AND; empty state "No pieces found".
- Route for CI screenshots: iOS `-startRoute search`, Android `--es route search`.

## Emojis (padronizados)
- Fluent Emoji Flat (Microsoft, MIT; licenca em design/emojis/LICENSE-fluent-emoji.txt), mesmas imagens no iOS e no Android. Apple Color Emoji nao pode ser redistribuido.
- Lista curada em design/emojis.json (166). `EmojiView` (iOS) / `EmojiImage` (Android); glifo fora da lista cai no emoji do sistema.
- Decisao do dono (2026-11-27): Fluent Flat + lista curada na v1.

## Collection (implemented)
- Header: back + centered chip. Meta line: "from X" (bold) left; @place, #n, status right. 2x2 grid, fixed tilts [-2.5, 2, 1.5, -2] deg, dashed "+" slots. Caption centered. Footer: timer + piece count, "Add piece" pill, info icon. Reached from a Folders card. Profile sheet (info icon) still to do.
- Parity: `fixtures/collection.json` (slots, tilts). CI route: `-startRoute collection` / `--es route collection`.

## Edit + Edit done (implemented)
- Left: cascade of 3 overlapping tilted cards (collection pieces + folder emoji). Right: 3 pill fields with numbered circles (title, place, optional tag). Action pill greyed until valid (`fixtures/edit.json`: title+place required, title <= 40, tag optional, normalized). After save: filled pills with check circles, amber check in header, block "Piece saved / Added to X / hint", pill becomes "Done".
- Reached from Collection ("+" slot or "Add piece"). Not persisted yet (SwiftData/Room wiring pending). CI routes: edit | editdone.

## Navigation + collection profile (implemented)
- Shelf rows and Folders cards open the Collection; "+" slots and "Add piece" open Edit; info icon opens the profile sheet (close X, big emoji, name, @place · #n, meta row, Pieces/Details pill + share, rows with leading chevron). Back returns to where the Collection was opened from.
- CI routes: `profile` (iOS `-startRoute profile`, Android `--es route profile`).
