import SwiftUI

/// Visualizador 3D da peca: a malha e projetada e sombreada em Canvas; arrastar gira, pinca da zoom.
struct ViewerView: View {
    @Environment(\.dismiss) private var dismiss
    let title: String
    @State private var yaw = -30.0
    @State private var pitch = 20.0
    @State private var scale = 1.0
    @State private var lastDrag = CGSize.zero
    @State private var baseScale = 1.0
    private let mesh = SampleShape.coinMesh()

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            ZStack {
                Text(title).font(.system(size: 17, weight: .semibold)).foregroundStyle(SuuviColor.text)
                HStack { IconButton(icon: "ic_chevron_left", label: "a11y.back") { dismiss() }; Spacer() }
            }
            .frame(height: 52)
            Canvas { ctx, size in draw(&ctx, size) }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(RoundedRectangle(cornerRadius: SuuviRadius.md).fill(SuuviColor.canvas))
                .gesture(
                    DragGesture()
                        .onChanged { v in
                            let r = ViewerRules.drag(yaw: yaw, pitch: pitch,
                                                     dx: Double(v.translation.width - lastDrag.width),
                                                     dy: Double(v.translation.height - lastDrag.height))
                            yaw = r.yaw; pitch = r.pitch; lastDrag = v.translation
                        }
                        .onEnded { _ in lastDrag = .zero }
                )
                .gesture(
                    MagnifyGesture()
                        .onChanged { scale = ViewerRules.zoom(scale: baseScale, pinch: Double($0.magnification)) }
                        .onEnded { _ in baseScale = scale }
                )
            Text("viewer.hint").font(.system(size: 15)).foregroundStyle(SuuviColor.textMuted)
        }
        .padding(.horizontal, SuuviSpace.s5)
        .padding(.bottom, SuuviSpace.s4)
        .background(SuuviColor.bg)
        .navigationBarBackButtonHidden(true)
    }

    private func draw(_ ctx: inout GraphicsContext, _ size: CGSize) {
        let ry = yaw * .pi / 180, rp = pitch * .pi / 180
        let cy = cos(ry), sy = sin(ry), cp = cos(rp), sp = sin(rp)
        let center = (x: 0.0, y: 0.0, z: -2.0)
        let pts: [(x: Double, y: Double, z: Double)] = mesh.vertices.map { v in
            let x = Double(v.x) - center.x, y = Double(v.y) - center.y, z = Double(v.z) + center.z
            let x1 = x * cy + z * sy, z1 = -x * sy + z * cy
            return (x1, y * cp - z1 * sp, y * sp + z1 * cp)
        }
        let s = Double(min(size.width, size.height)) / 36.0 * scale
        let mid = CGPoint(x: size.width / 2, y: size.height / 2)
        struct Face { let tri: [Int]; let depth: Double; let light: Double }
        var faces: [Face] = []
        for t in mesh.triangles {
            let a = pts[t[0]], b = pts[t[1]], c = pts[t[2]]
            var nx = (b.y - a.y) * (c.z - a.z) - (b.z - a.z) * (c.y - a.y)
            var ny = (b.z - a.z) * (c.x - a.x) - (b.x - a.x) * (c.z - a.z)
            var nz = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
            let len = (nx * nx + ny * ny + nz * nz).squareRoot()
            if len == 0 { continue }
            nx /= len; ny /= len; nz /= len
            let gx = (a.x + b.x + c.x) / 3, gy = (a.y + b.y + c.y) / 3, gz = (a.z + b.z + c.z) / 3
            if nx * gx + ny * gy + nz * gz < 0 { nx = -nx; ny = -ny; nz = -nz }  // normal para fora
            if nz >= 0 { continue }                                              // camera em -z: so faces voltadas para ela
            faces.append(Face(tri: t, depth: (a.z + b.z + c.z) / 3, light: 0.55 + 0.45 * max(0, -nz)))
        }
        for f in faces.sorted(by: { $0.depth > $1.depth }) {
            var path = Path()
            for (i, idx) in f.tri.enumerated() {
                let p = CGPoint(x: mid.x + pts[idx].x * s, y: mid.y + pts[idx].y * s)
                if i == 0 { path.move(to: p) } else { path.addLine(to: p) }
            }
            path.closeSubpath()
            let color = SuuviColor.accent.opacity(f.light)
            ctx.fill(path, with: .color(color))
            ctx.stroke(path, with: .color(color), lineWidth: 0.5)
        }
    }
}
