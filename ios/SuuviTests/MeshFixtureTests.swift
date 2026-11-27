import XCTest
@testable import Suuvi

final class MeshFixtureTests: XCTestCase {
    private struct Case: Decodable {
        let name: String; let contour: [[Int]]; let depth: Int
        let vertices: [[Int]]; let triangles: [[Int]]; let volume6: Int
    }
    private struct Fixture: Decodable { let cases: [Case] }

    func testPrisms() throws {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/mesh.json")
        let fx = try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
        for c in fx.cases {
            let pts = c.contour.map { IPoint(x: $0[0], y: $0[1]) }
            let mesh = try XCTUnwrap(MeshRules.extrude(pts, depth: c.depth), c.name)
            XCTAssertEqual(mesh.vertices, c.vertices.map { Vertex3(x: $0[0], y: $0[1], z: $0[2]) }, c.name)
            XCTAssertEqual(mesh.triangles, c.triangles, c.name)
            XCTAssertEqual(MeshRules.volume6(mesh), c.volume6, c.name)
            XCTAssertEqual(c.volume6, 3 * ContourRules.area2(pts) * c.depth, c.name)
        }
    }

    func testDegenerateReturnsNil() {
        XCTAssertNil(MeshRules.extrude([IPoint(x: 0, y: 0), IPoint(x: 1, y: 0)], depth: 1))
    }
}
