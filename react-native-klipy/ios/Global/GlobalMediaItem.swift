import SwiftUI

public struct GlobalMediaItem: Identifiable, Equatable {
  public static func == (lhs: GlobalMediaItem, rhs: GlobalMediaItem) -> Bool {
    return lhs.id == rhs.id
  }
  
  public var id: String
  var item: GridItemLayout
  var frame: CGRect
  
  init(id: String = UUID().uuidString, item: GridItemLayout, frame: CGRect) {
    self.id = id
    self.item = item
    self.frame = frame
  }
}
