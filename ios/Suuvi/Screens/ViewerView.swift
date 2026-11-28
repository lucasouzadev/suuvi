import SwiftUI

/// Visualizador 3D da peca: a malha e projetada e sombreada em Canvas; arrastar gira, pinca da zoom.
struct ViewerView: View {
    @Environment(\.dismiss) private var dismiss
    let title: String
    let image: String?
    /// Suave (Chaikin + bevel) ou low-poly (facetado); a escolha fica guardada.
    @State private var smooth: Bool
    @State private var model: Model?
    @State private var yaw = -30.0
    @State private var pitch = 20.0
    @State private var scale = 1.0
    @State private var lastDrag = CGSize.zero
    @State private var baseScale = 1.0

    /// Malha da peca + enquadramento; fica pronta depois de ler a imagem (fora do corpo da view).
    private struct Model {
        let mesh: PrismMesh
        let frame: (cx: Double, cy: Double, cz: Double, r: Double)
        /// Foto do recorte aplicada na face frontal (nil = cor lisa, ex.: a moeda de reserva).
        let texture: ShapeLoader.Textured?
        /// Aneis de vertices da malha: 4 suave (bevel), 2 low-poly.
        let rings: Int
    }

    init(title: String, image: String? = nil, startLow: Bool = false) {
        self.title = title
        self.image = image
        let saved = UserDefaults.standard.object(forKey: "viewerSmooth") as? Bool ?? true
        _smooth = State(initialValue: startLow ? false : saved)   // startLow: so o CI
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            ZStack {
                Text(title).font(.system(size: 17, weight: .semibold)).foregroundStyle(SuuviColor.text)
                HStack {
                    IconButton(icon: "ic_chevron_left", label: "a11y.back") { dismiss() }
                    Spacer()
                    IconButton(icon: smooth ? "ic_box" : "ic_circle", label: smooth ? "viewer.lowpoly" : "viewer.smooth") {
                        smooth.toggle()
                        UserDefaults.standard.set(smooth, forKey: "viewerSmooth")
                    }
                }
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
        .task(id: smooth) {
            // Recorte salvo da peca; sem recorte (foto opaca ou so emoji) cai na moeda de reserva.
            let rings = smooth ? 4 : 2
            let solid = ShapeLoader.solid(relative: image, smooth: smooth)
            let mesh = solid?.mesh ?? SampleShape.coinMesh(smooth: smooth)
            let textured = solid.flatMap { MeshRules.frontCap($0.mesh, rings: rings).isEmpty ? nil : $0 }
            model = Model(mesh: mesh, frame: ViewerRules.framing(mesh), texture: textured, rings: rings)
        }
    }

    /// Cor opaca (fundo -> acento): sem transparencia nao aparecem fios entre faces vizinhas.
    private func shade(_ light: Double) -> Color {
        func rgb(_ c: Color) -> (Double, Double, Double) {
            var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
            UIColor(c).getRed(&r, green: &g, blue: &b, alpha: &a)
            return (Double(r), Double(g), Double(b))
        }
        let bg = rgb(SuuviColor.canvas), fg = rgb(SuuviColor.accent)
        return Color(red: bg.0 + (fg.0 - bg.0) * light, green: bg.1 + (fg.1 - bg.1) * light, blue: bg.2 + (fg.2 - bg.2) * light)
    }

    private func draw(_ ctx: inout GraphicsContext, _ size: CGSize) {
        guard let model else { return }
        let mesh = model.mesh, f = model.frame, f0 = model.frame
        let ry = yaw * .pi / 180, rp = pitch * .pi / 180
        let cy = cos(ry), sy = sin(ry), cp = cos(rp), sp = sin(rp)
        let pts: [(x: Double, y: Double, z: Double)] = mesh.vertices.map { v in
            let x = Double(v.x) - f.cx, y = Double(v.y) - f.cy, z = Double(v.z) - f.cz
            let x1 = x * cy + z * sy, z1 = -x * sy + z * cy
            return (x1, y * cp - z1 * sp, y * sp + z1 * cp)
        }
        let s = Double(min(size.width, size.height)) / (2 * f.r) * scale
        let mid = CGPoint(x: size.width / 2, y: size.height / 2)
        struct Face { let tri: [Int]; let depth: Double; let light: Double; var cap: Path? = nil }
        var faces: [Face] = []
        // Face frontal com foto: os triangulos da tampa viram um unico desenho (so o 1o anel de vertices).
        let ring = mesh.vertices.count / model.rings
        var capPath = Path(), capCount = 0, capDepth = 0.0, capLight = 0.0
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
            let light = 0.55 + 0.45 * max(0, -nz)
            if model.texture != nil, t.allSatisfy({ $0 < ring }) {
                for (i, idx) in t.enumerated() {
                    let p = CGPoint(x: mid.x + pts[idx].x * s, y: mid.y + pts[idx].y * s)
                    if i == 0 { capPath.move(to: p) } else { capPath.addLine(to: p) }
                }
                capPath.closeSubpath()
                capCount += 1; capDepth += (a.z + b.z + c.z) / 3; capLight = light
                continue
            }
            faces.append(Face(tri: t, depth: (a.z + b.z + c.z) / 3, light: light))
        }
        if capCount > 0 { faces.append(Face(tri: [], depth: capDepth / Double(capCount), light: capLight, cap: capPath)) }
        for f in faces.sorted(by: { $0.depth > $1.depth }) {
            if let cap = f.cap, let tex = model.texture {
                ctx.fill(cap, with: .color(shade(f.light)))
                let m = ViewerRules.frontAffine(yaw: yaw, pitch: pitch, scale: scale, width: Double(size.width), height: Double(size.height),
                                                frame: f0, step: tex.step, ox: tex.ox, oy: tex.oy, unit: tex.unit)
                ctx.drawLayer { layer in
                    layer.clip(to: cap)
                    layer.concatenate(CGAffineTransform(a: m[0], b: m[1], c: m[2], d: m[3], tx: m[4], ty: m[5]))
                    layer.draw(Image(decorative: tex.image, scale: 1), in: CGRect(x: 0, y: 0, width: tex.image.width, height: tex.image.height))
                }
                ctx.fill(cap, with: .color(.black.opacity((1 - f.light) * 0.6)))   // sombra suave quando inclinada
                continue
            }
            var path = Path()
            for (i, idx) in f.tri.enumerated() {
                let p = CGPoint(x: mid.x + pts[idx].x * s, y: mid.y + pts[idx].y * s)
                if i == 0 { path.move(to: p) } else { path.addLine(to: p) }
            }
            path.closeSubpath()
            let color = shade(f.light)
            ctx.fill(path, with: .color(color))
            ctx.stroke(path, with: .color(color), lineWidth: 0.5)
        }
    }
}
