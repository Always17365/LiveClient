//
//  LiveRoomInfoItemObject.h
//  dating
//
//  Created by Alex on 17/5/23.
//  Copyright © 2017年 qpidnetwork. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <httpcontroller/HttpRequestEnum.h>

@interface LiveRoomInfoItemObject : NSObject
{

}
/**
 * Hot结构体
 * userId			主播ID
 * nickName          主播昵称
 * photoUrl		    主播头像url
 * onlineStatus		主播在线状态
 * roomPhotoUrl		直播间封面图url
 * roomType          直播间类型
 * interest          爱好ID列表
 * anchorType        主播类型（1:白银 2:黄金）
 */

@property (nonatomic, strong) NSString* userId;
@property (nonatomic, strong) NSString* nickName;
@property (nonatomic, strong) NSString* photoUrl;
// 直播间状态（0:离线（Offline） 正在直播（Live））
@property (nonatomic, assign) OnLineStatus onlineStatus;
@property (nonatomic, strong) NSString* roomPhotoUrl;
// 直播间类型（0:（没有直播间） 1:（免费公开直播间） 2:（付费公开直播间） 3:（普通私密直播间） 4:（豪华私密直播间））
@property (nonatomic, assign) HttpRoomType roomType;
// 爱好ID列表
 @property (nonatomic, strong) NSMutableArray<NSNumber*>* interest;
//InterestType interest[INTERESTTYPE_END];
//主播类型（1:白银 2:黄金）
@property (nonatomic, assign) AnchorLevelType anchorType;

// - (void)SetInterestWithIndex:(InterestType)type index:(int)index;

@end