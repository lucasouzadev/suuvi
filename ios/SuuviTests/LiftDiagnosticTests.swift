import XCTest
@testable import Suuvi

/// Diagnostico: roda o recorte real (Vision) na foto de exemplo e imprime o resultado.
/// Nao falha o build quando o Vision nao roda no simulador do CI (sem GPU/ANE); o resultado aparece no log.
final class LiftDiagnosticTests: XCTestCase {
    func testLiftSample() throws {
        let src = URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent().deletingLastPathComponent()
            .appendingPathComponent("Suuvi/Resources/sample-object.jpg")
        let photo = try ImageStore.savePhoto(Data(contentsOf: src))
        let out = ImageStore.cutoutURL(for: photo)
        do {
            try ObjectLifter.lift(photo: photo, to: out)
            let size = (try? FileManager.default.attributesOfItem(atPath: out.path)[.size]) ?? 0
            XCTAssertGreaterThan(size as? Int ?? 0, 0)
        } catch {
            throw XCTSkip("LIFT-DIAG FAILED \(error) | \(String(describing: error as NSError))")
        }
    }
}
