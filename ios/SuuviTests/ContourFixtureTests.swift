import XCTest
@testable import Suuvi

final class ContourFixtureTests: XCTestCase {
    private struct Trace: Decodable { let name: String; let rows: [String]; let expect: [[Int]]; let area2: Int }
    private struct Simp: Decodable { let name: String; let points: [[Int]]; let eps: Int; let expect: [[Int]] }
    private struct Fixture: Decodable { let trace: [Trace]; let simplify: [Simp] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/contour.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }
    private func pts(_ a: [[Int]]) -> [IPoint] { a.map { IPoint(x: $0[0], y: $0[1]) } }

    func testTrace() throws {
        for c in try load().trace {
            let rows = c.rows.map { Array($0) }
            let got = ContourRules.trace(width: rows[0].count, height: rows.count) { x, y in rows[y][x] == "#" }
            XCTAssertEqual(got, pts(c.expect), c.name)
            XCTAssertEqual(ContourRules.area2(got), c.area2, c.name)
        }
    }

    func testSimplify() throws {
        for c in try load().simplify {
            XCTAssertEqual(ContourRules.simplify(pts(c.points), eps: c.eps), pts(c.expect), c.name)
        }
    }
}
