import XCTest
@testable import Suuvi

final class FolderFixtureTests: XCTestCase {
    private struct Entry: Decodable { let name: String; let lastUsed: Int }
    private struct Case: Decodable { let name: String; let items: [Entry]; let expect: [String] }
    private struct Fixture: Decodable { let order: [Case] }

    func testOrder() throws {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/folders.json")
        let fixture = try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
        for c in fixture.order {
            let items = c.items.map { FolderItem(name: $0.name, emoji: "", place: "", number: 0, lastUsedEpochDay: $0.lastUsed) }
            XCTAssertEqual(FolderRules.ordered(items).map(\.name), c.expect, c.name)
        }
    }
}
