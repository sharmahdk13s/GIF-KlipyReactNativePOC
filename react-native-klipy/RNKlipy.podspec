Pod::Spec.new do |s|
  s.name         = "RNKlipy"
  s.version      = "0.1.1"
  s.summary      = "React Native wrapper for Klipy"
  s.license      = "MIT"
  s.author       = { "Klipy" => "support@klipy.com" }
  s.homepage     = "https://klipy.com"
  s.source       = { :git => "https://example.com/react-native-klipy.git", :tag => s.version.to_s }

  s.platforms    = { :ios => "13.0" }
  s.source_files = "ios/**/*.{h,m,mm,cpp,swift}"
  s.private_header_files = "ios/**/*.h"

  s.dependency "SDWebImage"
  s.dependency "SDWebImageSwiftUI"
  s.dependency "AlertToast"
  s.dependency "Alamofire"
  s.dependency "Moya"

  if respond_to?(:install_modules_dependencies, true)
    install_modules_dependencies(s)
  else
    s.dependency "React-Core"
  end

  s.pod_target_xcconfig = {
    'HEADER_SEARCH_PATHS' => '$(inherited) $(PODS_ROOT)/../build/generated/ios $(PODS_ROOT)/../../node_modules/react-native'
  }
  # TODO: add your real native Klipy iOS SDK dependency here when it exists
  # s.dependency "KlipySDK"
end
