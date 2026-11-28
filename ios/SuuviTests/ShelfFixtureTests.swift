import XCTest
@testable import Suuvi

final class ShelfFixtureTests: XCTestCase {
    private struct Daily: Decodable { let name: String; let count: Int; let epochDay: Int; let expect: Int? }
    private struct Chip: Decodable { let title: String; let expect: String }
    private struct Key: Decodable { let folder: String; let number: Int }
    private struct Order: Decodable { let name: String; let keys: [Key]; let expect: [Int] }
    private struct Fixture: Decodable { let dailyPick: [Daily]; let chipLetter: [Chip]; let dailyOrder: [Order] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/shelf.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testDailyPick() throws {
        for c in try load().dailyPick {
            XCTAssertEqual(ShelfRules.dailyIndex(epochDay: c.epochDay, count: c.count), c.expect, c.name)
        }
    }

    func testDailyOrder() throws {
        for c in try load().dailyOrder {
            XCTAssertEqual(ShelfRules.dailyOrder(c.keys.map { ($0.folder, $0.number) }), c.expect, c.name)
        }
    }

    func testChipLetter() throws {
        for c in try load().chipLetter {
            XCTAssertEqual(ShelfRules.chipLetter(c.title), c.expect, c.title)
        }
    }
}
