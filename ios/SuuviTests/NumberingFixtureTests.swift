import XCTest
@testable import Suuvi

final class NumberingFixtureTests: XCTestCase {
    private struct Case: Decodable {
        let name: String
        let existing: [Int]
        let lastIssued: Int?
        let expect: Int
    }
    private struct Fixture: Decodable { let cases: [Case] }

    func testSharedFixtureCases() throws {
        // ios/SuuviTests/<arquivo> -> repo/fixtures/numbering.json
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/numbering.json")
        let fixture = try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
        XCTAssertFalse(fixture.cases.isEmpty)
        for c in fixture.cases {
            XCTAssertEqual(Numbering.next(existing: c.existing, lastIssued: c.lastIssued ?? 0), c.expect, c.name)
        }
    }
}
