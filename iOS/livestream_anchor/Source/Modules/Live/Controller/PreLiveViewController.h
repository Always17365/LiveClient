//
//  PreLiveViewController.h
//  livestream
//
//  Created by Max on 2017/9/4.
//  Copyright © 2017年 net.qdating. All rights reserved.
//

#import "LSGoogleAnalyticsViewController.h"
#import "LiveRoom.h"

typedef enum PreLiveStatus {
    PreLiveStatus_None = 0,
    PreLiveStatus_Inviting,
    PreLiveStatus_JoinTo,
    PreLiveStatus_Accept,
    PreLiveStatus_Error,
} PreLiveStatus;

@interface PreLiveViewController : LSGoogleAnalyticsViewController
#pragma mark - 直播间信息
@property (nonatomic, strong) LiveRoom *liveRoom;

// 当前状态
@property (nonatomic, assign) PreLiveStatus status;

#pragma mark - 界面
@property (nonatomic, weak) IBOutlet UIImageView *bgImageView;
@property (nonatomic, weak) IBOutlet UIImageView *ladyImageView;
@property (nonatomic, weak) IBOutlet UILabel* statusLabel;
@property (nonatomic, weak) IBOutlet UILabel* handleCountDownLabel;
@property (nonatomic, weak) IBOutlet UILabel* ladyNameLabel;
@property (nonatomic, weak) IBOutlet UILabel* tipsLabel;
@property (nonatomic, weak) IBOutlet UILabel* countDownLabel;
@property (nonatomic, weak) IBOutlet UIButton *cancelButton;

@property (nonatomic, weak) IBOutlet UIButton *retryButton;
@property (nonatomic, weak) IBOutlet NSLayoutConstraint *retryButtonHeight;
@property (weak, nonatomic) IBOutlet UIButton *closeButton;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *closeButtonTop;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *closeButtonHeight;

@property (nonatomic, weak) IBOutlet UIActivityIndicatorView *loadingView;

@end