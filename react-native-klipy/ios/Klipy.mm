#import "Klipy.h"
#import <React/RCTUtils.h>
#import <AVKit/AVKit.h>
#import <WebKit/WebKit.h>

// Access Swift types (KlipyMediaPickerHost) exposed from the RNKlipy module.
#if __has_include(<RNKlipy/RNKlipy-Swift.h>)
#import <RNKlipy/RNKlipy-Swift.h>
#elif __has_include("RNKlipy-Swift.h")
#import "RNKlipy-Swift.h"
#endif

@class KlipyMediaPickerHost;

static NSString *klipyApiKey = nil;

@implementation Klipy

// Classic React Native bridge module registration. The exported name must
// match the key used from JS (NativeModules.NativeKlipy).
RCT_EXPORT_MODULE(NativeKlipy);

// Synchronous initialize method (no Promise blocks expected from JS).
RCT_EXPORT_METHOD(initialize:(NSString *)apiKey options:(NSDictionary *)options)
{
  klipyApiKey = apiKey;
  // TODO: Use apiKey and options to configure Klipy as needed.
}

// Open the media picker hosted by KlipyMediaPickerHost.
RCT_EXPORT_METHOD(open)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    UIViewController *rootViewController = RCTPresentedViewController();
    if (rootViewController != nil) {
      [KlipyMediaPickerHost.shared open:rootViewController];
      [self setMediaPickerVisible:YES];
    }
  });
}

// Expose a setter for media picker visibility so JS can control bottom sheet
// style behaviour similarly to Android.
RCT_EXPORT_METHOD(setMediaPickerVisible:(BOOL)visible)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [[NSNotificationCenter defaultCenter] postNotificationName:@"KlipyMediaPickerVisibilityChanged"
                                                        object:nil
                                                      userInfo:@{ @"visible": @(visible) }];
  });
}

@end
