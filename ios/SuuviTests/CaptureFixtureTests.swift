import XCTest
@testable import Suuvi

final class CaptureFixtureTests: XCTestCase {
    private struct Alpha: Decodable { let name: String; let rows: [String]; let expect: [Int]? }
    private struct Crop: Decodable { let name: String; let box: [Int]; let image: [Int]; let expect: [Int] }
    private struct Cta: Decodable { let photos: Int; let lifted: Int; let lifting: Bool; let expect: String }
    private struct Fixture: Decodable { let maxPhotos: Int; let alphaBounds: [Alpha]; let paddedCrop: [Crop]; let cta: [Cta] }

    private func load() throws -> Fixture {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/capture.json")
        return try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
    }

    func testMax() throws { XCTAssertEqual(try load().maxPhotos, CaptureRules.maxPhotos) }

    func testAlphaBounds() throws {
        for c in try load().alphaBounds {
            let h = c.rows.count, w = c.rows[0].count
            let alpha = c.rows.flatMap { $0.map { $0 == "#" ? UInt8(255) : UInt8(0) } }
            let got = CaptureRules.alphaBounds(alpha: alpha, width: w, height: h)
            let want = c.expect.map { PixelRect(x: $0[0], y: $0[1], w: $0[2], h: $0[3]) }
            XCTAssertEqual(got, want, c.name)
        }
    }

    func testPaddedCrop() throws {
        for c in try load().paddedCrop {
            let r = CaptureRules.paddedCrop(PixelRect(x: c.box[0], y: c.box[1], w: c.box[2], h: c.box[3]),
                                            imageWidth: c.image[0], imageHeight: c.image[1])
            XCTAssertEqual(r, PixelRect(x: c.expect[0], y: c.expect[1], w: c.expect[2], h: c.expect[3]), c.name)
        }
    }

    func testCta() throws {
        for c in try load().cta {
            XCTAssertEqual(CaptureRules.cta(photos: c.photos, lifted: c.lifted, lifting: c.lifting).rawValue, c.expect)
        }
    }
}
