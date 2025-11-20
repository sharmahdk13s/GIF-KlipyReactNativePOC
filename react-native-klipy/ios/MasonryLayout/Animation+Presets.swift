import SwiftUI

// Animation presets used by the masonry grid / infinite scroll.
extension Animation {
  /// Smooth sheet-like animation used when loading more items.
  static var smoothSheet: Animation {
    .spring(response: 0.5, dampingFraction: 0.7, blendDuration: 0.3)
  }
}
