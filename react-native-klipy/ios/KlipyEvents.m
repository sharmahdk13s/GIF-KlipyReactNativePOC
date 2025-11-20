#import "KlipyEvents.h"

static NSString * const kKlipyReactionSelectedNotification = @"KlipyReactionSelected";

@implementation KlipyEvents {
  BOOL _hasListeners;
}

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents
{
  return @[ @"onReactionSelected" ];
}

- (void)startObserving
{
  _hasListeners = YES;
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(handleReactionSelectedNotification:)
                                               name:kKlipyReactionSelectedNotification
                                             object:nil];
}

- (void)stopObserving
{
  _hasListeners = NO;
  [[NSNotificationCenter defaultCenter] removeObserver:self
                                                  name:kKlipyReactionSelectedNotification
                                                object:nil];
}

- (void)handleReactionSelectedNotification:(NSNotification *)notification
{
  if (!_hasListeners) {
    return;
  }
  NSDictionary *payload = notification.userInfo ?: @{};
  [self sendEventWithName:@"onReactionSelected" body:payload];
}

@end
