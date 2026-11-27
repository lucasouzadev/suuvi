import XCTest
@testable import Suuvi

final class CollectionFixtureTests: XCTestCase {
    private struct SlotCase: Decodable { let count: Int; let expect: String }
    private struct Fixture: Decodable { let slots: [SlotCase]; let tilts: [Double] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/collection.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testSlots() throws {
        for c in try load().slots {
            let got = CollectionRules.slots(count: c.count).map { $0 ? "F" : "E" }.joined()
            XCTAssertEqual(got, c.expect, "count \(c.count)")
        }
    }

    func testTilts() throws {
        let f = try load()
        XCTAssertEqual(f.tilts, CollectionRules.tilts)
    }
}
