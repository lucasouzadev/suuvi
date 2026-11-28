import XCTest
@testable import Suuvi

final class ViewerFixtureTests: XCTestCase {
    private struct Drag: Decodable { let name: String; let yaw: Double; let pitch: Double; let dx: Double; let dy: Double; let expect: [Double] }
    private struct Zoom: Decodable { let name: String; let scale: Double; let pinch: Double; let expect: Double }
    private struct Framing: Decodable { let name: String; let vertices: [[Int]]; let expect: [Double] }
    private struct Fixture: Decodable { let drag: [Drag]; let zoom: [Zoom]; let framing: [Framing] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/viewer.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testDrag() throws {
        for c in try load().drag {
            let r = ViewerRules.drag(yaw: c.yaw, pitch: c.pitch, dx: c.dx, dy: c.dy)
            XCTAssertEqual(r.yaw, c.expect[0], accuracy: 1e-9, c.name)
            XCTAssertEqual(r.pitch, c.expect[1], accuracy: 1e-9, c.name)
        }
    }

    func testZoom() throws {
        for c in try load().zoom { XCTAssertEqual(ViewerRules.zoom(scale: c.scale, pinch: c.pinch), c.expect, accuracy: 1e-9, c.name) }
    }

    func testFraming() throws {
        for c in try load().framing {
            let m = PrismMesh(vertices: c.vertices.map { Vertex3(x: $0[0], y: $0[1], z: $0[2]) }, triangles: [])
            let f = ViewerRules.framing(m)
            XCTAssertEqual([f.cx, f.cy, f.cz, f.r].count, c.expect.count, c.name)
            for (got, want) in zip([f.cx, f.cy, f.cz, f.r], c.expect) { XCTAssertEqual(got, want, accuracy: 1e-9, c.name) }
        }
    }

    func testSampleShapeIsClosedMesh() {
        let m = SampleShape.coinMesh()
        XCTAssertGreaterThan(m.triangles.count, 20)
        XCTAssertTrue(MeshRules.isClosed(m))
        XCTAssertGreaterThan(MeshRules.volume6(m), 0)
    }
}
