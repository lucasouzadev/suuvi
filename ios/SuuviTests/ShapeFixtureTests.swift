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
        let solid: Solid?
        let texture: Tex?

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
    private struct Tex: Decodable { let step: Int; let ox: Int; let oy: Int }
    private struct Solid: Decodable { let vertices: Int; let triangles: Int; let bbox: [Int] }
    private struct Chaikin: Decodable { let name: String; let points: [[Int]]; let rounds: Int; let expect: [[Int]] }
    private struct Fixture: Decodable { let cases: [Case]; let chaikin: [Chaikin] }

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
            let tex = try XCTUnwrap(c.texture, c.name)
            XCTAssertEqual([s?.step, s?.ox, s?.oy], [tex.step, tex.ox, tex.oy], c.name)
            guard let m = ShapeRules.mesh(alpha: a, width: w, height: h) else { XCTFail("sem malha: \(c.name)"); continue }
            let sol = try XCTUnwrap(c.solid, c.name)
            XCTAssertEqual(m.vertices.count, sol.vertices, c.name)
            XCTAssertEqual(m.triangles.count, sol.triangles, c.name)
            let xs = m.vertices.map(\.x), ys = m.vertices.map(\.y), zs = m.vertices.map(\.z)
            XCTAssertEqual([xs.min()!, ys.min()!, zs.min()!, xs.max()!, ys.max()!, zs.max()!], sol.bbox, c.name)
            XCTAssertTrue(MeshRules.isClosed(m), c.name)
            XCTAssertEqual(MeshRules.frontCap(m).count, m.vertices.count / 4 - 2, c.name)
            XCTAssertTrue(MeshRules.frontCap(m).allSatisfy { $0.allSatisfy { m.vertices[$0].z == 0 } }, c.name)
            XCTAssertGreaterThan(MeshRules.volume6(m), 0, c.name)
        }
    }

    func testChaikin() throws {
        for c in try load().chaikin {
            let out = MeshRules.chaikin(c.points.map { IPoint(x: $0[0], y: $0[1]) }, rounds: c.rounds)
            XCTAssertEqual(out.map { [$0.x, $0.y] }, c.expect, c.name)
        }
    }
}
