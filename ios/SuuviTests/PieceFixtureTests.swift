import XCTest
@testable import Suuvi

final class PieceFixtureTests: XCTestCase {
    private struct Expect: Decodable { let number: Int; let title: String; let place: String; let tag: String?; let note: String?; let lastIssued: Int }
    private struct Case: Decodable {
        let name: String; let existing: [Int]; let lastIssued: Int
        let title: String; let place: String; let tag: String; let note: String?; let expect: Expect?
    }
    private struct Fixture: Decodable { let cases: [Case] }

    private struct Img: Decodable { let name: String; let cutouts: [String?]; let photos: [String]; let expect: String? }
    private struct ImgFixture: Decodable { let image: [Img] }

    func testImagePath() throws {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/create.json")
        for c in try JSONDecoder().decode(ImgFixture.self, from: Data(contentsOf: url)).image {
            XCTAssertEqual(PieceRules.imagePath(cutouts: c.cutouts, photos: c.photos), c.expect, c.name)
        }
    }

    private struct Upd: Decodable {
        struct E: Decodable { let title: String; let place: String; let tag: String?; let note: String? }
        let name: String; let title: String; let place: String; let tag: String; let note: String?; let expect: E?
    }
    private struct Iss: Decodable { let name: String; let existing: [Int]; let stored: Int; let expect: Int }
    private struct UpdFixture: Decodable { let update: [Upd]; let issued: [Iss] }

    func testUpdateAndIssued() throws {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/create.json")
        let fx = try JSONDecoder().decode(UpdFixture.self, from: Data(contentsOf: url))
        for c in fx.update {
            let want = c.expect.map { EditedPiece(title: $0.title, place: $0.place, tag: $0.tag, note: $0.note) }
            XCTAssertEqual(PieceRules.update(title: c.title, place: c.place, tag: c.tag, note: c.note ?? ""), want, c.name)
        }
        for c in fx.issued { XCTAssertEqual(PieceRules.issued(existing: c.existing, stored: c.stored), c.expect, c.name) }
    }

    func testCreate() throws {
        let url = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("fixtures/create.json")
        let fx = try JSONDecoder().decode(Fixture.self, from: Data(contentsOf: url))
        for c in fx.cases {
            let got = PieceRules.create(title: c.title, place: c.place, tag: c.tag, note: c.note ?? "", existing: c.existing, lastIssued: c.lastIssued)
            let want = c.expect.map { NewPiece(number: $0.number, title: $0.title, place: $0.place, tag: $0.tag, note: $0.note, lastIssued: $0.lastIssued) }
            XCTAssertEqual(got, want, c.name)
        }
    }
}
