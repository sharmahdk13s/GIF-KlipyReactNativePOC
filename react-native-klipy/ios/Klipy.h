// Classic React Native bridge module interface for Klipy.
// This avoids requiring the generated TurboModule header (NativeKlipySpec).
#import <React/RCTBridgeModule.h>

@interface Klipy : NSObject<RCTBridgeModule>
@end
