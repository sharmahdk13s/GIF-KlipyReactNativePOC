//
//  ContentPushingMediaPickerModifier.swift
//  KlipyiOS-demo
//
//  Created by Tornike Gomareli on 17.03.25.
//

import SwiftUI
import Combine

private extension Notification.Name {
  static let klipyMediaPickerVisibilityChanged =
    Notification.Name("KlipyMediaPickerVisibilityChanged")
}

enum SheetHeight {
  case half
  case full
  
  func value(for screenHeight: CGFloat) -> CGFloat {
    switch self {
    case .half:
      return screenHeight * 0.5
    case .full:
      return screenHeight * 0.88
    }
  }
}

struct ContentPushingMediaPickerModifier: ViewModifier {
  @Binding var isPresented: Bool
  let onSend: (GridItemLayout) -> Void
  @Binding var previewItem: GlobalMediaItem?
  
  @Binding var heightState: SheetHeight
  
  @State private var keyboardHeight: CGFloat = 0
  
  // Theme Color matches the view
  private let sheetBackgroundColor = Color(red: 44/255, green: 44/255, blue: 46/255)
  
  private let keyboardWillShowPublisher = NotificationCenter.default.publisher(for: UIResponder.keyboardWillShowNotification)
  private let keyboardWillHidePublisher = NotificationCenter.default.publisher(for: UIResponder.keyboardWillHideNotification)
  private let klipyVisibilityPublisher = NotificationCenter.default.publisher(for: .klipyMediaPickerVisibilityChanged)
  
  @State private var dragOffset: CGFloat = 0
  
  private var dragGesture: some Gesture {
    DragGesture()
      .onChanged { gesture in
        dragOffset = gesture.translation.height
      }
      .onEnded { gesture in
        // Simple threshold for expanding/collapsing
        let threshold: CGFloat = 50
        
        if gesture.translation.height > threshold {
            // Drag Down -> Collapse or Dismiss Keyboard
            if heightState == .full {
                withAnimation(.spring()) { heightState = .half }
            }
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        } else if gesture.translation.height < -threshold {
            // Drag Up -> Expand
            if heightState == .half {
                withAnimation(.spring()) { heightState = .full }
            }
        }
        
        withAnimation {
            dragOffset = 0
        }
      }
  }
  
  init(
    isPresented: Binding<Bool>,
    onSend: @escaping (GridItemLayout) -> Void,
    previewItem: Binding<GlobalMediaItem?>,
    heightState: Binding<SheetHeight>
  ) {
    self._isPresented = isPresented
    self.onSend = onSend
    self._previewItem = previewItem
    self._heightState = heightState
  }
  
  func body(content: Content) -> some View {
    if #available(iOS 17.0, *) {
      GeometryReader { geometry in
        // Use the full screen height to position the sheet relative to the
        // keyboard. Using geometry.size.height (which excludes safe areas)
        // would leave a visible gap equal to the bottom safe area.
        let screenHeight = UIScreen.main.bounds.height
        let safeAreaBottom = geometry.safeAreaInsets.bottom
        let safeAreaTop = geometry.safeAreaInsets.top
        
        ZStack(alignment: .bottom) {
          // 1. Main Content
          content
              .zIndex(0)
              .frame(maxWidth: .infinity, maxHeight: .infinity)

          // 2. Dimmer Overlay
          if isPresented {
            Color.black.opacity(0.4)
              .ignoresSafeArea()
              .onTapGesture {
                isPresented = false
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
              }
              .transition(.opacity)
              .zIndex(1)
          }
          
          // 3. Bottom Sheet
          if isPresented {
            VStack(spacing: 0) {
              // Handle Area - ZStack prevents structural changes (Fixes Focus Loss)
              ZStack {
                  Capsule()
                      .fill(Color.gray.opacity(0.5))
                      .frame(width: 40, height: 5)
                      .padding(.top, 10)
                      .padding(.bottom, 5)
              }
              .frame(height: 20) // Fixed height for handle area
              .frame(maxWidth: .infinity)
              .background(sheetBackgroundColor)
              .opacity(1)
              .gesture(dragGesture)

              DynamicMediaViewWrapper(
                onSend: onSend,
                previewItem: $previewItem,
                sheetHeight: $heightState
              )
              
              // Internal Spacing for Home Indicator
              // When Keyboard Closed: Add space so buttons sit above home indicator
              // When Keyboard Open: 0 space, buttons sit on keyboard
              .padding(.bottom, keyboardHeight > 0 ? 0 : max(safeAreaBottom * 0.3, 4))
            }
            // Calculate Precise Frame
            .frame(height: calculateHeight(screenHeight: screenHeight, topSafe: safeAreaTop, bottomSafe: safeAreaBottom))
            .background(sheetBackgroundColor)
            .cornerRadius(24, corners: [.topLeft, .topRight])
            .shadow(color: Color.black.opacity(0.3), radius: 10, x: 0, y: -5)
            
            // IGNORE SAFE AREA: We handle bottom padding manually above
            .edgesIgnoringSafeArea(.bottom)
            
            // Lift by the full keyboard height so the sheet's bottom edge
            // aligns exactly with the top of the keyboard.
            .padding(.bottom, keyboardHeight)
            .offset(y: dragOffset)
            .zIndex(2)
            .transition(.move(edge: .bottom))
          }
        }
        // Crucial: Prevent automatic keyboard avoidance which causes double-push
        .ignoresSafeArea(.keyboard)
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: isPresented)
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: dragOffset)
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: heightState)
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: keyboardHeight)
        
        .onReceive(keyboardWillShowPublisher) { notification in
          if let keyboardFrame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect {
            let newHeight = keyboardFrame.height
            if abs(keyboardHeight - newHeight) > 1 {
                keyboardHeight = newHeight
            }
          }
        }
        .onReceive(keyboardWillHidePublisher) { _ in
          keyboardHeight = 0
        }
        .onReceive(klipyVisibilityPublisher) { notification in
          if let visible = notification.userInfo?["visible"] as? Bool {
            isPresented = visible
          }
        }
      }
    } else {
      content
    }
  }
    
  private func calculateHeight(screenHeight: CGFloat, topSafe: CGFloat, bottomSafe: CGFloat) -> CGFloat {
      if keyboardHeight > 0 {
          // KEYBOARD OPEN LOGIC
          // Height = full screen minus keyboard minus top safe area.
          // Combined with .padding(.bottom, keyboardHeight) above this makes
          // the sheet fill exactly the area between the top safe area and the
          // top of the keyboard.
          return screenHeight - keyboardHeight - topSafe
      } else {
          // KEYBOARD CLOSED LOGIC
          // Use the configured half/full height and float a bit above the
          // home indicator by adding the bottom safe area.
          return heightState.value(for: screenHeight) + bottomSafe
      }
  }
}

// MARK: - Helpers
extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

extension View {
  func contentPushingMediaPicker(
    isPresented: Binding<Bool>,
    onSend: @escaping (GridItemLayout) -> Void,
    previewItem: Binding<GlobalMediaItem?>,
    heightState: Binding<SheetHeight>
  ) -> some View {
    self.modifier(ContentPushingMediaPickerModifier(
      isPresented: isPresented,
      onSend: onSend,
      previewItem: previewItem,
      heightState: heightState
    ))
  }
}

struct DynamicMediaViewWrapper: View {
  let onSend: (GridItemLayout) -> Void
  @Binding var previewItem: GlobalMediaItem?
  @Binding var sheetHeight: SheetHeight
  
  var body: some View {
      if #available(iOS 17.0, *) {
          DynamicMediaView(
            onSend: { mediaItem in
                onSend(mediaItem)
            },
            previewItem: $previewItem,
            sheetHeight: $sheetHeight
          )
      } else {
          EmptyView()
      }
  }
}

struct SheetHandleButton: View {
    var onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 4) {
                Image(systemName: "chevron.compact.down")
                    .font(.system(size: 22))
                    .foregroundColor(.secondary)
            }
            .padding(.vertical, 8)
            .frame(width: 80, height: 36)
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
    }
}