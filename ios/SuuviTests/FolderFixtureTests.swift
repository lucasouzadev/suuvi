import XCTest
@testable import Suuvi

final class FolderFixtureTests: XCTestCase {
    private struct Entry: Decodable { let name: String; let lastUsed: Int }
    private struct Case: Decodable { let name: String; let items: [Entry]; let expect: [String] }
    private struct Use: Decodable { let name: String; let base: Int; let days: [Int]; let expect: Int }
    private struct Fixture: Decodable { let order: [Case]; let lastUsed: [Use] }

    func testLastUsed() throws {
        for c in try load().lastUsed { XCTAssertEqual(FolderRules.lastUsed(base: c.base, pieceDays: c.days), c.expect, c.name) }
    }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/folders.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testOrder() throws {
        for c in try load().order {
            let items = c.items.map { FolderItem(name: $0.name, emoji: "", place: "", number: 0, lastUsedEpochDay: $0.lastUsed) }
            XCTAssertEqual(FolderRules.ordered(items).map(\.name), c.expect, c.name)
        }
    }
}
