import Foundation
import shared

class ShareGatewayIos: ShareGateway {
    func sharePerformance(performance: Performance) {
        NSLog("Sharing performance: %@ (%@%%) - %@", performance.title, performance.positivity, performance.description)
    }
}
