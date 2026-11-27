import Foundation

/// Linha da lista da Shelf (resumo de uma colecao).
struct ShelfItem: Identifiable {
    let id = UUID()
    let name: String
    let emoji: String
    let place: String
    let tier: PieceTier
    let number: Int
    let status: SouvenirStatus
}

struct DailyPick {
    let emoji: String
    let title: String
    let note: String
    let epochDay: Int
    let place: String
}

/// Dados de exemplo ate a Shelf ler do banco local.
enum SampleData {
    static let collection: [ShelfItem] = [
        ShelfItem(name: "Lisboa", emoji: "🏺", place: "lisboa", tier: .coin, number: 12, status: .ready),
        ShelfItem(name: "Café", emoji: "☕️", place: "cafe", tier: .magnet, number: 5, status: .draft),
        ShelfItem(name: "Feira", emoji: "🌿", place: "feira", tier: .pin, number: 7, status: .failed),
    ]
    static let received: [ShelfItem] = [
        ShelfItem(name: "Praia", emoji: "🐚", place: "ana", tier: .coin, number: 3, status: .ready),
        ShelfItem(name: "Mercado", emoji: "🍎", place: "bia", tier: .pin, number: 2, status: .draft),
    ]
    static let dailyPicks: [DailyPick] = [
        DailyPick(emoji: "🏺", title: "Moeda de Lisboa", note: "Achada numa feira de rua, num domingo de sol.", epochDay: 20150, place: "lisboa"),
        DailyPick(emoji: "☕️", title: "Ímã do café", note: "O primeiro café depois da viagem.", epochDay: 20210, place: "cafe"),
        DailyPick(emoji: "🌿", title: "Pin da feira", note: "Uma folha que ficou na bolsa.", epochDay: 20300, place: "feira"),
    ]
}
