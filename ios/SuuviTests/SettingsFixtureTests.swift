import XCTest
@testable import Suuvi

final class SettingsFixtureTests: XCTestCase {
    private struct NameCase: Decodable { let name: String; let input: String; let expect: String }
    private struct Fixture: Decodable { let normalizeName: [NameCase]; let tiers: [String] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/settings.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testNormalizeName() throws {
        for c in try load().normalizeName {
            XCTAssertEqual(SettingsRules.normalizeName(c.input), c.expect, c.name)
        }
    }

    func testTiers() throws {
        XCTAssertEqual(try load().tiers, SettingsRules.tiers)
    }
}
