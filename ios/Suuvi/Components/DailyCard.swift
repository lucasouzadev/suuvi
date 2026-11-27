import SwiftUI

/// Card "Suvenir do dia": titulo, nota de ate 3 linhas, data e lugar.
struct DailyCard: View {
    let pick: DailyPick

    private var dateText: String {
        Date(timeIntervalSince1970: Double(pick.epochDay) * 86_400)
            .formatted(.dateTime.day().month(.abbreviated).year().locale(.current))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: SuuviSpace.s2) {
            HStack(spacing: SuuviSpace.s1) {
                Text("shelf.daily.heading")
                Text(pick.emoji)
                Text(pick.title)
            }
            .font(.system(size: 17, weight: .semibold))
            .foregroundStyle(SuuviColor.text)
            Text(pick.note)
                .font(.system(size: 15))
                .foregroundStyle(SuuviColor.text)
                .lineLimit(3)
            HStack {
                HStack(spacing: SuuviSpace.s1) {
                    SuuviIcon(name: "ic_calendar", size: 14)
                    Text(dateText)
                }
                Spacer()
                HStack(spacing: SuuviSpace.s1) {
                    SuuviIcon(name: "ic_map_pin", size: 14)
                    Text("@\(pick.place)")
                }
            }
            .font(.system(size: 13))
            .foregroundStyle(SuuviColor.textMuted)
        }
        .padding(SuuviSpace.s4)
        .frame(maxWidth: .infinity, alignment: .leading)
        .overlay(
            RoundedRectangle(cornerRadius: SuuviRadius.md)
                .stroke(SuuviColor.border, lineWidth: 1)
        )
        .accessibilityElement(children: .combine)
    }
}
