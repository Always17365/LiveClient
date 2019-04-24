/*
 * author: Alex
 *   date: 2018-05-12
 *   file:  .h
 *   desc: 10.12.发送多人互动直播间文本消息
 */

#pragma once

#include "ITask.h"
#include <string>

using namespace std;

class SendHangoutLiveChatTask : public ITask
{
public:
    SendHangoutLiveChatTask(void);
    virtual ~SendHangoutLiveChatTask(void);
    
    // ITask接口函数
public:
    // 初始化
    virtual bool Init(IImClientListener* listener);
    // 处理已接收数据
    virtual bool Handle(const TransportProtocol& tp);
    // 获取待发送的Json数据
    virtual bool GetSendData(Json::Value& data);
    // 获取命令号
    virtual string GetCmdCode() const;
    // 设置seq
    virtual void SetSeq(SEQ_T seq);
    // 获取seq
    virtual SEQ_T GetSeq() const;
    // 是否需要等待回复。若false则发送后释放(delete掉)，否则发送后会被添加至待回复列表，收到回复后释放
    virtual bool IsWaitToRespond() const;
    // 获取处理结果
    virtual void GetHandleResult(LCC_ERR_TYPE& errType, string& errMsg);
    // 未完成任务的断线通知
    virtual void OnDisconnect();
    
public:
    // 初始化参数
    bool InitParam(const string& roomId, const string& nickName, const string& msg, const list<string>& at);
    
private:
    IImClientListener*    m_listener;
    
    SEQ_T               m_seq;        // seq
    
    LCC_ERR_TYPE        m_errType;    // 服务器返回的处理结果
    string              m_errMsg;    // 服务器返回的结果描述
    
    string              m_roomId;       // 直播间ID
    string              m_nickName;     // 发送者昵称
    string              m_msg;          // 发送的信息
    list<string>        m_at;           // 用户ID，用于指定接收者（字符串数组）（可无，无则表示发送给直播间所有人）

};
