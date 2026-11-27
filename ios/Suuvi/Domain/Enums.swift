import Foundation

enum PieceTier: String, Codable, CaseIterable {
    case pin, magnet, coin
    static let `default`: PieceTier = .coin
}

enum SouvenirStatus: String, Codable {
    case draft, ready, failed
}
