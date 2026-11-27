import XCTest
@testable import Suuvi

final class SearchFixtureTests: XCTestCase {
    private struct Entry: Decodable { let id: String; let name: String; let place: String; let tags: [String] }
    private struct Case: Decodable { let name: String; let query: String; let selected: [String]; let expect: [String] }
    private struct Fixture: Decodable { let items: [Entry]; let filter: [Case]; let allTags: [String] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/search.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    private func items(_ f: Fixture) -> [SearchItem] {
        f.items.map { SearchItem(id: $0.id, name: $0.name, emoji: "", place: $0.place, number: 0, tags: $0.tags) }
    }

    func testFilter() throws {
        let f = try load()
        for c in f.filter {
            let got = SearchRules.filter(items(f), query: c.query, selected: Set(c.selected)).map(\.id)
            XCTAssertEqual(got, c.expect, c.name)
        }
    }

    func testAllTags() throws {
        let f = try load()
        XCTAssertEqual(SearchRules.allTags(items(f)), f.allTags)
    }
}
