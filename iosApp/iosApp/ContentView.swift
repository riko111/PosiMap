import SwiftUI
import shared

struct ContentView: View {
    private let repository = PerformanceRepository(shareGateway: ShareGateway())

    var body: some View {
        let performances = repository.getPerformances() as? [Performance] ?? []
        return NavigationView {
            List(performances, id: .id) { performance in
                VStack(alignment: .leading, spacing: 8) {
                    Text("\(performance.title) (\(performance.positivity)%)")
                        .font(.headline)
                    Text(performance.description)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .onTapGesture {
                    repository.sharePerformance(performance: performance)
                }
            }
            .navigationTitle("PosiMap")
        }
    }
}

#Preview {
    ContentView()
}
