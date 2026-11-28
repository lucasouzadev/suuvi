import XCTest
@testable import Suuvi

final class ShapeFixtureTests: XCTestCase {
    private struct Case: Decodable {
        let name: String
        let rows: [String]?
        let size: [Int]?
        let rect: [Int]?
        let points: [[Int]]?
        let depth: Int?

        func alpha() -> (a: [UInt8], w: Int, h: Int) {
            if let rows = rows {
                let w = rows[0].count
                let a = rows.flatMap { $0.map { ch -> UInt8 in ch == "#" ? 255 : (ch == "o" ? 10 : 0) } }
                return (a, w, rows.count)
            }
            let w = size![0], h = size![1], r = rect!
            var a = [UInt8](repeating: 0, count: w * h)
            for y in r[1]..<r[3] { for x in r[0]..<r[2] { a[y * w + x] = 255 } }
            return (a, w, h)
        }
    }
    private struct Fixture: Decodable { let cases: [Case] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/shape.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testShapes() throws {
        for c in try load().cases {
            let (a, w, h) = c.alpha()
            let s = ShapeRules.shape(alpha: a, width: w, height: h)
            guard let expect = c.points else { XCTAssertNil(s, c.name); continue }
            XCTAssertEqual(s?.points.map { [$0.x, $0.y] }, expect, c.name)
            XCTAssertEqual(s?.depth, c.depth, c.name)
            guard let m = ShapeRules.mesh(alpha: a, width: w, height: h) else { XCTFail("sem malha: \(c.name)"); continue }
            XCTAssertEqual(m.vertices.count, expect.count * 2, c.name)
            XCTAssertGreaterThan(MeshRules.volume6(m), 0, c.name)
        }
    }
}
