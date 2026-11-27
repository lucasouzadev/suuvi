import XCTest
@testable import Suuvi

final class EditFixtureTests: XCTestCase {
    private struct ValidCase: Decodable { let name: String; let title: String; let place: String; let expect: Bool }
    private struct TagCase: Decodable { let `in`: String; let expect: String }
    private struct Fixture: Decodable { let valid: [ValidCase]; let tag: [TagCase] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/edit.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testValid() throws {
        for c in try load().valid {
            XCTAssertEqual(EditRules.isValid(title: c.title, place: c.place), c.expect, c.name)
        }
    }

    func testTag() throws {
        for c in try load().tag {
            XCTAssertEqual(EditRules.normalizeTag(c.in), c.expect, c.in)
        }
    }
}
