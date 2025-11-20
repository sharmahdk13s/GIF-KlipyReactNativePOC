import UIKit
import SwiftUI

@objc public class KlipyMediaPickerHost: NSObject {
  @objc public static let shared = KlipyMediaPickerHost()

  @objc public func open(_ rootViewController: UIViewController) {
    // Close any active keyboard
    UIApplication.shared.sendAction(
      #selector(UIResponder.resignFirstResponder),
      to: nil,
      from: nil,
      for: nil
    )

    // After a short delay (similar to toggleMediaPicker), present a simple
    // media picker host controller. You can replace this with your full
    // SwiftUI-based Klipy UI later.
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
      let pickerVC = KlipyMediaPickerViewController()
      pickerVC.modalPresentationStyle = .overFullScreen
      rootViewController.present(pickerVC, animated: true)
    }
  }
}

final class KlipyMediaPickerViewController: UIViewController {
  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = UIColor.black.withAlphaComponent(0.4)

    if #available(iOS 17.0, *) {
      let mediaPickerView = StandaloneMediaPickerView(onDismiss: { [weak self] in
        self?.dismiss(animated: true)
      })
      let hostingController = UIHostingController(rootView: mediaPickerView)
      addChild(hostingController)
      hostingController.view.translatesAutoresizingMaskIntoConstraints = false
      hostingController.view.backgroundColor = .clear

      view.addSubview(hostingController.view)

      NSLayoutConstraint.activate([
        hostingController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
        hostingController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        hostingController.view.topAnchor.constraint(equalTo: view.topAnchor),
        hostingController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
      ])

      hostingController.didMove(toParent: self)
    } else {
      // Fallback: keep the simple UIKit bottom sheet for older iOS versions.
      let sheetView = UIView()
      sheetView.translatesAutoresizingMaskIntoConstraints = false
      sheetView.backgroundColor = UIColor(named: "#36383F") ?? UIColor.darkGray
      sheetView.layer.cornerRadius = 16
      sheetView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
      sheetView.layer.masksToBounds = true

      view.addSubview(sheetView)

      let sheetHeight = view.bounds.height * 0.5

      NSLayoutConstraint.activate([
        sheetView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
        sheetView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        sheetView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        sheetView.heightAnchor.constraint(equalToConstant: sheetHeight)
      ])

      let titleLabel = UILabel()
      titleLabel.text = "Klipy Media Picker"
      titleLabel.textColor = .white
      titleLabel.font = UIFont.systemFont(ofSize: 16, weight: .semibold)
      titleLabel.translatesAutoresizingMaskIntoConstraints = false

      sheetView.addSubview(titleLabel)

      NSLayoutConstraint.activate([
        titleLabel.centerXAnchor.constraint(equalTo: sheetView.centerXAnchor),
        titleLabel.topAnchor.constraint(equalTo: sheetView.topAnchor, constant: 12)
      ])
    }
  }
}

@available(iOS 17.0, *)
struct StandaloneMediaPickerView: View {
  @State private var isPresented: Bool = true
  @State private var previewItem: GlobalMediaItem? = nil
  @State private var sheetHeight: SheetHeight = .half
  let onDismiss: () -> Void

  init(onDismiss: @escaping () -> Void) {
    self.onDismiss = onDismiss
  }

  var body: some View {
    Color.clear
      .ignoresSafeArea()
      .contentPushingMediaPicker(
        isPresented: $isPresented,
        onSend: handleSend,
        previewItem: $previewItem,
        heightState: $sheetHeight
      )
      .onChange(of: isPresented) { _, newValue in
        if !newValue {
          onDismiss()
        }
      }
  }

  private func handleSend(_ item: GridItemLayout) {
    sheetHeight = .half
    isPresented = false

    let reactionId = item.slug
    let lowerType = item.type.lowercased()
    let reactionType: String
    switch lowerType {
    case "gif", "gifs":
      reactionType = "gif"
    case "clip", "clips":
      reactionType = "clip"
    case "sticker", "stickers":
      reactionType = "sticker"
    default:
      reactionType = "gif"
    }
    let reactionValue = item.highQualityUrl.isEmpty ? item.url : item.highQualityUrl

    let extra: [String: Any] = [
      "slug": item.slug,
      "url": item.url,
      "highQualityUrl": item.highQualityUrl,
      "previewUrl": item.previewUrl,
      "title": item.title
    ]

    NotificationCenter.default.post(
      name: Notification.Name("KlipyReactionSelected"),
      object: nil,
      userInfo: [
        "id": reactionId,
        "type": reactionType,
        "value": reactionValue,
        "extra": extra
      ]
    )
  }
}
