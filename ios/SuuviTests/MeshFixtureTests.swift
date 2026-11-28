import XCTest
@testable import Suuvi

final class MeshFixtureTests: XCTestCase {
    private struct Case: Decodable {
        let name: String; let contour: [[Int]]; let depth: Int
        let vertices: [[Int]]; let triangles: [[Int]]; let volume6: Int
    }
    private struct Del: Decodable { let name: String; let contour: [[Int]]; let expect: [[Int]] }
    private struct Bev: Decodable {
        let name: String; let contour: [[Int]]; let depth: Int; let bevel: Int
        let vertices: [[Int]]; let triangles: [[Int]]
    }
    private struct Fixture: Decodable { let cases: [Case]; let delaunay: [Del]; let beveled: [Bev] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/mesh.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testDelaunay() throws {
        for c in try load().delaunay {
            let pts = c.contour.map { IPoint(x: $0[0], y: $0[1]) }
            let ears = try XCTUnwrap(MeshRules.earClip(pts), c.name)
            XCTAssertEqual(MeshRules.delaunay(pts, ears), c.expect, c.name)
        }
    }

    func testBeveled() throws {
        for c in try load().beveled {
            let pts = c.contour.map { IPoint(x: $0[0], y: $0[1]) }
            let mesh = try XCTUnwrap(MeshRules.beveled(pts, depth: c.depth, bevel: c.bevel), c.name)
            XCTAssertEqual(mesh.vertices, c.vertices.map { Vertex3(x: $0[0], y: $0[1], z: $0[2]) }, c.name)
            XCTAssertEqual(mesh.triangles, c.triangles, c.name)
            XCTAssertTrue(MeshRules.isClosed(mesh), c.name)
            XCTAssertGreaterThan(MeshRules.volume6(mesh), 0, c.name)
        }
    }

    func testPrisms() throws {
        let fx = try load()
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
