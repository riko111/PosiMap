import Foundation
import shared

class ShareGatewayIos: ShareGateway {
    override func share(bytes: KotlinByteArray, filename: String, mime: String) {
            let data = Data(bytes: bytes, count: Int(bytes.size))
            let url = FileManager.default.temporaryDirectory.appendingPathComponent(filename)
            try? data.write(to: url)

            let vc = UIActivityViewController(activityItems: [url], applicationActivities: nil)
            UIApplication.shared.windows.first?.rootViewController?.present(vc, animated: true)
        }
}
