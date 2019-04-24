package com.qpidnetwork.livemodule.liveshow.livechat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qpidnetwork.livemodule.R;
import com.qpidnetwork.livemodule.livechat.LCMessageItem;
import com.qpidnetwork.livemodule.livechat.LCSystemLinkItem;
import com.qpidnetwork.livemodule.livechat.LCTextItem;
import com.qpidnetwork.livemodule.livechat.LCWarningLinkItem;
import com.qpidnetwork.livemodule.livechat.LiveChatManager;
import com.qpidnetwork.livemodule.livemessage.item.LiveMessageItem;
import com.qpidnetwork.livemodule.liveshow.LiveModule;
import com.qpidnetwork.livemodule.liveshow.livechat.downloader.LivechatVoiceDownloader;
import com.qpidnetwork.livemodule.liveshow.livechat.downloader.MagicIconImageDownloader;
import com.qpidnetwork.livemodule.liveshow.model.NoMoneyParamsBean;
import com.qpidnetwork.livemodule.utils.CustomerHtmlTagHandler;
import com.qpidnetwork.livemodule.utils.DateUtil;
import com.qpidnetwork.livemodule.utils.DisplayUtil;
import com.qpidnetwork.livemodule.view.MaterialProgressBar;
import com.qpidnetwork.qnbridgemodule.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 直播liveChat专用adapter
 * @author Jagger 2018-11-17
 */
public class LiveChatTalkAdapter extends RecyclerView.Adapter<LiveChatTalkAdapter.MessageViewHolder> {

    private static final int TYPE_DEFAULT = 99;
    private static final int TYPE_WARNING = 10;
    private static final int TYPE_SYSTEM = 11;
    private static final int TYPE_CUSTOM = 12;
    private static final int TYPE_TEXT_MSG_RECV = 13;
    private static final int TYPE_TEXT_MSG_SEND = 14;
    private static final int TYPE_TEXT_VOICE_RECV = 15;
    private static final int TYPE_TEXT_VOICE_SEND = 16;
    private static final int TYPE_TEXT_MAGIC_ICON_RECV = 17;
    private static final int TYPE_TEXT_MAGIC_ICON_SEND = 18;
    private static final int TYPE_NOTIFY = 19;

    //格式化
    SimpleDateFormat weekFormat=new SimpleDateFormat("EEEE HH:mm", Locale.ENGLISH);
    SimpleDateFormat weekBeforeDateformat=new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
    SimpleDateFormat todayDateformat=new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private Context mContext;
//    private LiveChatManager mLiveChatManager;
//    private VoicePlayerManager mVoicePlayerManager;
    private ExpressionImageGetter imageGetter;      /* 文本表情转化 */

    //列表数据
    private List<LCMessageItem> mMsgList = new ArrayList<>();

    //表情解析
    private CustomerHtmlTagHandler.Builder mBuilder;


    public LiveChatTalkAdapter(Context context , List<LCMessageItem> list){
        mContext = context;
        mMsgList = list;
        //emoji解析
        int emojiWidth = (int)context.getResources().getDimension(R.dimen.live_size_16dp);
        int emojiHeight = (int)context.getResources().getDimension(R.dimen.live_size_16dp);
        mBuilder = new CustomerHtmlTagHandler.Builder();
        mBuilder.setContext(context)
                .setGiftImgHeight(emojiWidth)
                .setGiftImgWidth(emojiHeight);
        imageGetter = new ExpressionImageGetter(context, DisplayUtil.dip2px( context, 28), DisplayUtil.dip2px(context, 28));
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = super.getItemViewType(position);  //java.lang.RuntimeException: -1 is already used for view type Header
//        Log.i("Jagger" , "LiveChatTalkAdapter getItemViewType position:" + position);
        //在范围内
        if(position < mMsgList.size()){
            LCMessageItem item = mMsgList.get(position);
            if(item != null){
                switch (item.sendType) {
                    case Recv:
                        switch (item.msgType) {
                            case Text:
                                viewType = TYPE_TEXT_MSG_RECV;
                                break;
                            case Voice:
                                viewType = TYPE_TEXT_VOICE_RECV;
                                break;
                            case MagicIcon:
                                viewType = TYPE_TEXT_MAGIC_ICON_RECV;
                                break;
                            case Warning:
                                viewType = TYPE_WARNING;
                                break;
                            case System:
                                viewType = TYPE_SYSTEM;
                                break;
                            default:
                                break;
                        }
                        break;
                    case Send:
                        switch (item.msgType) {
                            case Text:
                                viewType = TYPE_TEXT_MSG_SEND;
                                break;
                            case Warning:
                                viewType = TYPE_WARNING;
                                break;
                            case Voice:
                                viewType = TYPE_TEXT_VOICE_SEND;
                                break;
                            case MagicIcon:
                                viewType = TYPE_TEXT_MAGIC_ICON_SEND;
                                break;
                            case Custom:
                                viewType = TYPE_CUSTOM;
                                break;
                            case System:
                                viewType = TYPE_SYSTEM;
                                break;
                            default:
                                break;
                        }
                        break;
                    case System:
                        switch (item.msgType) {
                            case Warning:
                                viewType = TYPE_WARNING;
                                break;
                            case System:
                                viewType = TYPE_SYSTEM;
                                break;
                            case Notify:
                                viewType = TYPE_NOTIFY;
                                break;
                            default:
                                break;
                        }
                        break;
                    case Unknow:
                        switch (item.msgType) {
                            case Warning:
                                viewType = TYPE_WARNING;
                                break;
                            case System:
                                viewType = TYPE_SYSTEM;
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return (mMsgList == null || mMsgList.size() == 0) ? 0 : mMsgList.size();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MessageViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_WARNING:
                viewHolder = new WarningTypeViewHolder(inflater.inflate(R.layout.item_live_chat_warning_tips, parent, false));
                break;
            case TYPE_SYSTEM:
                viewHolder = new SystemTypeViewHolder(inflater.inflate(R.layout.item_live_chat_normal_notify, parent, false));
                break;
            case TYPE_CUSTOM:
                viewHolder = new CustomTypeViewHolder(inflater.inflate(R.layout.item_live_chat_trychat_notify, parent, false));
                break;
            case TYPE_TEXT_MSG_RECV:
                viewHolder = new RecvTextTypeViewHolder(inflater.inflate(R.layout.item_live_chat_message_in, parent, false));
                break;
            case TYPE_TEXT_MSG_SEND:
                viewHolder = new SendTextTypeViewHolder(inflater.inflate(R.layout.item_live_chat_message_out, parent, false));
                break;
            case TYPE_TEXT_VOICE_RECV:
                viewHolder = new RecvVoiceViewHolder(inflater.inflate(R.layout.item_live_chat_in_voice, parent, false));
                break;
            case TYPE_TEXT_VOICE_SEND:
                viewHolder = new SendVoiceViewHolder(inflater.inflate(R.layout.item_live_chat_out_voice, parent, false));
                break;
            case TYPE_TEXT_MAGIC_ICON_RECV:
                viewHolder = new RecvMagicIconViewHolder(inflater.inflate(R.layout.item_live_chat_magicicon_in, parent, false));
                break;
            case TYPE_TEXT_MAGIC_ICON_SEND:
                viewHolder = new SendMagicIconViewHolder(inflater.inflate(R.layout.item_live_chat_magicicon_out, parent, false));
                break;
            case TYPE_NOTIFY:
                viewHolder = new NotifyTypeViewHolder(inflater.inflate(R.layout.item_live_chat_session_pause_notify, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        //选项
        final LCMessageItem bean = mMsgList.get(position);

        if (holder instanceof WarningTypeViewHolder) {
            WarningTypeViewHolder warningTypeViewHolder = (WarningTypeViewHolder) holder;

            if (bean.getWarningItem() != null
                    && bean.getWarningItem().linkItem != null
                    && (bean.getWarningItem().linkItem.linkOptType == LCWarningLinkItem.LinkOptType.Rechange)) {
                String tips = bean.getWarningItem().message + " " + bean.getWarningItem().linkItem.linkMsg;
                SpannableString sp = new SpannableString(tips);
                ClickableSpan clickableSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO 买点
                        LiveModule.getInstance().onAddCreditClick(mContext , new NoMoneyParamsBean());
                    }
                };
                sp.setSpan(new StyleSpan(Typeface.BOLD),
                        bean.getWarningItem().message.length() + 1, tips.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(clickableSpan,
                        bean.getWarningItem().message.length() + 1, tips.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                warningTypeViewHolder.tvNotifyMsg.setText(sp);
                warningTypeViewHolder.tvNotifyMsg.setLinkTextColor(mContext.getResources().getColor(
                        R.color.blue_color));
                warningTypeViewHolder.tvNotifyMsg.setMovementMethod(LinkMovementMethod.getInstance());
                warningTypeViewHolder.tvNotifyMsg.setFocusable(false);
                warningTypeViewHolder.tvNotifyMsg.setClickable(false);
                warningTypeViewHolder.tvNotifyMsg.setLongClickable(false);
            } else {
                warningTypeViewHolder.tvNotifyMsg.setText(bean.getWarningItem().message);
            }
        } else if (holder instanceof SystemTypeViewHolder){
            SystemTypeViewHolder systemTypeViewHolder = (SystemTypeViewHolder) holder;

            if ((bean.getSystemItem().linkItem != null)
                    && (bean.getSystemItem().linkItem.linkOptType == LCSystemLinkItem.SystemLinkOptType.Theme_reload
                    ||bean.getSystemItem().linkItem.linkOptType == LCSystemLinkItem.SystemLinkOptType.Theme_recharge)) {
                String tips = bean.getSystemItem().message + " "
                        + bean.getSystemItem().linkItem.linkMsg;
                SpannableString sp = new SpannableString(tips);
                ClickableSpan clickableSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        if (bean.getSystemItem().linkItem != null){
                            if(bean.getSystemItem().linkItem.linkOptType == LCSystemLinkItem.SystemLinkOptType.Theme_reload){
//                                ((ChatActivity) mContext).loadTheme();
                            }else if(bean.getSystemItem().linkItem.linkOptType == LCSystemLinkItem.SystemLinkOptType.Theme_recharge){
//                                ((ChatActivity) mContext).renewCurrentTheme();
                            }
                        }
                    }
                };
                sp.setSpan(new StyleSpan(Typeface.BOLD),
                        bean.getSystemItem().message.length() + 1, tips.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(clickableSpan,
                        bean.getSystemItem().message.length() + 1, tips.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                systemTypeViewHolder.tvNotifyMsg.setText(sp);
                systemTypeViewHolder.tvNotifyMsg.setLinkTextColor(mContext.getResources().getColor(
                        R.color.blue_color));
                systemTypeViewHolder.tvNotifyMsg.setMovementMethod(LinkMovementMethod.getInstance());
                systemTypeViewHolder.tvNotifyMsg.setFocusable(false);
                systemTypeViewHolder.tvNotifyMsg.setClickable(false);
                systemTypeViewHolder.tvNotifyMsg.setLongClickable(false);
            } else {
                systemTypeViewHolder.tvNotifyMsg.setText(bean.getSystemItem().message);
            }
        }else if (holder instanceof CustomTypeViewHolder){
            CustomTypeViewHolder customTypeViewHolder = (CustomTypeViewHolder) holder;

            customTypeViewHolder.tvNotifyMsg.setText(mContext.getString(R.string.live_chat_try_tips));
        }else if (holder instanceof RecvTextTypeViewHolder){
            RecvTextTypeViewHolder recvTextTypeViewHolder = (RecvTextTypeViewHolder) holder;

            recvTextTypeViewHolder.chat_message.setText(imageGetter.getExpressMsgHTML(bean.getTextItem().message));
        }else if (holder instanceof SendTextTypeViewHolder){
            SendTextTypeViewHolder sendTextTypeViewHolder = (SendTextTypeViewHolder) holder;

            LCTextItem textItem = bean.getTextItem();
            String text = textItem.message;
            sendTextTypeViewHolder.chat_message.setText(imageGetter.getExpressMsgHTML(text));
            if (bean.getTextItem().illegal) {
                /* 非法的，显示警告 */
                sendTextTypeViewHolder.includeWaring.setVisibility(View.VISIBLE);
                sendTextTypeViewHolder.tvNotifyMsg.setText(mContext
                        .getResources().getString(
                                R.string.live_chat_lady_illeage_message));
            }
            if (bean.statusType == LCMessageItem.StatusType.Processing) {
                sendTextTypeViewHolder.pbDownload.setVisibility(View.VISIBLE);
                sendTextTypeViewHolder.btnError.setVisibility(View.GONE);
            } else if (bean.statusType == LCMessageItem.StatusType.Fail) {
                sendTextTypeViewHolder.pbDownload.setVisibility(View.GONE);
//                sendTextTypeViewHolder.btnError.setTag(bean);
                sendTextTypeViewHolder.btnError.setVisibility(View.VISIBLE);
                sendTextTypeViewHolder.btnError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onErrorClicked(bean);
                    }
                });
            } else {
                sendTextTypeViewHolder.pbDownload.setVisibility(View.GONE);
                sendTextTypeViewHolder.btnError.setVisibility(View.GONE);
            }
        }else if (holder instanceof RecvVoiceViewHolder){
            RecvVoiceViewHolder recvVoiceViewHolder = (RecvVoiceViewHolder)holder;

            //
            recvVoiceViewHolder.llayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onVoiceItemClick(view, bean);
                }
            });
            //
            recvVoiceViewHolder.chat_sound_time.setText(bean.getVoiceItem().timeLength + "''");
            recvVoiceViewHolder.chat_sound_time.setTag(position);
            //是否已读 add by Jagger2017-6-15
            recvVoiceViewHolder.img_isread.setVisibility(bean.getVoiceItem().isRead(mContext)?View.GONE:View.VISIBLE);
            //
            new LivechatVoiceDownloader(mContext).downloadAndPlayVoice(recvVoiceViewHolder.pbDownload, recvVoiceViewHolder.btnError, bean);
        }else if (holder instanceof SendVoiceViewHolder){
            SendVoiceViewHolder sendVoiceViewHolder = (SendVoiceViewHolder)holder;

            sendVoiceViewHolder.llayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onVoiceItemClick(view, bean);
                }
            });
            //
            if (bean.statusType == LCMessageItem.StatusType.Processing) {
                sendVoiceViewHolder.pbDownload.setVisibility(View.VISIBLE);
                sendVoiceViewHolder.btnError.setVisibility(View.GONE);
            } else if (bean.statusType == LCMessageItem.StatusType.Fail) {
                sendVoiceViewHolder.pbDownload.setVisibility(View.GONE);
//                sendVoiceViewHolder.btnError.setTag(bean);
                sendVoiceViewHolder.btnError.setVisibility(View.VISIBLE);
                sendVoiceViewHolder.btnError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onErrorClicked(bean);
                    }
                });
            }else {
                sendVoiceViewHolder.pbDownload.setVisibility(View.GONE);
                sendVoiceViewHolder.btnError.setVisibility(View.GONE);
            }
            sendVoiceViewHolder.timeView.setText(bean.getVoiceItem().timeLength + "''");
//            sendVoiceViewHolder.timeView.setTag(position);
            //
            new LivechatVoiceDownloader(mContext).downloadAndPlayVoice(null, null, bean);
        }else if (holder instanceof RecvMagicIconViewHolder){
            RecvMagicIconViewHolder recvMagicIconViewHolder = (RecvMagicIconViewHolder)holder;

            recvMagicIconViewHolder.btnError.setTag(position);
            new MagicIconImageDownloader(mContext).displayMagicIconPhoto(
                    recvMagicIconViewHolder.ivMagicIconPhoto, recvMagicIconViewHolder.pbDownload, bean, recvMagicIconViewHolder.btnError);
        }else if (holder instanceof SendMagicIconViewHolder){
            SendMagicIconViewHolder sendMagicIconViewHolder = (SendMagicIconViewHolder)holder;

            new MagicIconImageDownloader(mContext).displayMagicIconPhoto(
                    sendMagicIconViewHolder.ivMagicIcon, null, bean, null);
            if (bean.statusType == LCMessageItem.StatusType.Processing) {
                sendMagicIconViewHolder.pbDownload.setVisibility(View.VISIBLE);
                sendMagicIconViewHolder.btnError.setVisibility(View.GONE);
            } else if (bean.statusType == LCMessageItem.StatusType.Fail) {
                sendMagicIconViewHolder.pbDownload.setVisibility(View.GONE);
//                sendMagicIconViewHolder.btnError.setTag(bean);
                sendMagicIconViewHolder.btnError.setVisibility(View.VISIBLE);
                sendMagicIconViewHolder.btnError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onErrorClicked(bean);
                    }
                });
            }else {
                sendMagicIconViewHolder.pbDownload.setVisibility(View.GONE);
                sendMagicIconViewHolder.btnError.setVisibility(View.GONE);
            }
        }else if (holder instanceof NotifyTypeViewHolder){
            NotifyTypeViewHolder notifyTypeViewHolder = (NotifyTypeViewHolder) holder;

            String username = bean.fromId;
            if( LiveChatManager.getInstance().GetLadyInfoById( bean.fromId) != null){
                username = LiveChatManager.getInstance().GetLadyInfoById( bean.fromId).userName;
            }
            String sessionDesc = String.format(mContext.getString(R.string.livechat_seesion_pause_tips), username);
            notifyTypeViewHolder.tvDesc.setText(sessionDesc);
        }
    }

    /**
     * 格式化指定时间，增加当日／昨天／一周内／一周前逻辑判断
     * @param time
     * @return
     */
    private String getDateFormatString(long time){
        String dateFormatString = "";
        DateUtil.DateTimeType timeType = DateUtil.getDateTimeType(time);
        if(timeType == DateUtil.DateTimeType.Today){
            dateFormatString = todayDateformat.format(new Date(time));
        }else if(timeType == DateUtil.DateTimeType.Yestoday){
            dateFormatString = mContext.getResources().getString(R.string.message_contactlist_dateformat_yesterday) + " " + todayDateformat.format(new Date(time));
        }else if(timeType == DateUtil.DateTimeType.InWeek){
            dateFormatString = weekFormat.format(new Date(time));
        }else if(timeType == DateUtil.DateTimeType.WeekBefore){
            dateFormatString = weekBeforeDateformat.format(new Date(time));
        }
        return dateFormatString;
    }

    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    /**
     * 菜单点击事件c
     */
    public interface OnItemClickListener{
        //点击重发按钮
        void onResendClick(LiveMessageItem messageItem);
        //点击充值
        void onAddCreditClick();
        //点击错误
        void onErrorClicked(LCMessageItem lcMessageItem);
        //点击语音
        void onVoiceItemClick(View v, LCMessageItem lcMessageItem);
        //点击重发
//        void onResend(LCMessageItem lcMessageItem);
    }

    //-------------------------item数据模型 start------------------------------

    //-------------------------item数据模型 end------------------------------


    //-------------------------ViewHolder数据模型 start--------------------------

    //ViewHolder模型
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }

    //警告类型布局 ok
    public class WarningTypeViewHolder extends MessageViewHolder {
        public TextView tvNotifyMsg;
        public WarningTypeViewHolder(View itemView) {
            super(itemView);
            tvNotifyMsg = (TextView) itemView.findViewById(R.id.tvNotifyMsg);
        }
    }

    //系统消息类型布局 ok
    public class SystemTypeViewHolder extends MessageViewHolder {
        public TextView tvNotifyMsg;
        public SystemTypeViewHolder(View itemView) {
            super(itemView);
            tvNotifyMsg = (TextView) itemView.findViewById(R.id.tvNotifyMsg);
        }
    }

    //Custom类型布局 ok
    public class CustomTypeViewHolder extends MessageViewHolder {
        public TextView tvNotifyMsg;
        public CustomTypeViewHolder(View itemView) {
            super(itemView);
            tvNotifyMsg = (TextView) itemView.findViewById(R.id.tvNotifyMsg);
        }
    }

    //文本接收样式 ok
    public class RecvTextTypeViewHolder extends MessageViewHolder {
        public TextView chat_message;

        public RecvTextTypeViewHolder(View itemView) {
            super(itemView);
            chat_message = (TextView) itemView.findViewById(R.id.chat_message);
        }
    }

    //文本发送样式 ok
    public class SendTextTypeViewHolder extends MessageViewHolder {
        public TextView chat_message;
        public MaterialProgressBar pbDownload;
        public View includeWaring;
        public ImageButton btnError;
        public TextView tvNotifyMsg;

        public SendTextTypeViewHolder(View itemView) {
            super(itemView);
            chat_message = (TextView) itemView.findViewById(R.id.chat_message);
            pbDownload = (MaterialProgressBar) itemView.findViewById(R.id.pbDownload);
            includeWaring = itemView.findViewById(R.id.includeWaring);
            btnError = (ImageButton) itemView.findViewById(R.id.btnError);
            tvNotifyMsg = (TextView) itemView.findViewById(R.id.tvNotifyMsg);
        }
    }

    //语音接收样式 ok
    public class RecvVoiceViewHolder extends MessageViewHolder {
        public LinearLayout llayout ;
        public MaterialProgressBar pbDownload;
        public ImageButton btnError;
        public TextView chat_sound_time;
        public ImageView img_isread;

        public RecvVoiceViewHolder(View itemView) {
            super(itemView);
            llayout = (LinearLayout)itemView.findViewById(R.id.chat_sound);
            pbDownload = (MaterialProgressBar) itemView.findViewById(R.id.pbDownload);
            btnError = (ImageButton) itemView.findViewById(R.id.btnError);
            chat_sound_time = (TextView) itemView.findViewById(R.id.chat_sound_time);
            img_isread = (ImageView)itemView.findViewById(R.id.img_isread);
        }
    }

    //语音发送样式 ok
    public class SendVoiceViewHolder extends MessageViewHolder {
        public LinearLayout llayout ;
        public MaterialProgressBar pbDownload;
        public ImageButton btnError;
        public TextView timeView;

        public SendVoiceViewHolder(View itemView) {
            super(itemView);
            llayout = (LinearLayout)itemView.findViewById(R.id.chat_sound);
            pbDownload = (MaterialProgressBar) itemView.findViewById(R.id.pbDownload);
            btnError = (ImageButton) itemView.findViewById(R.id.btnError);
            timeView = (TextView) itemView.findViewById(R.id.chat_sound_time);
        }
    }

    //小高表接收样式 ok
    public class RecvMagicIconViewHolder extends MessageViewHolder {
        public MaterialProgressBar pbDownload;
        public ImageView ivMagicIconPhoto;
        public TextView chat_sound_time;
        public ImageButton btnError;

        public RecvMagicIconViewHolder(View itemView) {
            super(itemView);
            pbDownload = (MaterialProgressBar) itemView.findViewById(R.id.pbDownload);
            ivMagicIconPhoto = (ImageView)itemView.findViewById(R.id.ivMagicIcon);
            btnError = (ImageButton) itemView.findViewById(R.id.btnError);
        }
    }

    //小高表发送样式 ok
    public class SendMagicIconViewHolder extends MessageViewHolder {
        public LinearLayout llayout ;
        public MaterialProgressBar pbDownload ;
        public ImageButton btnError;
        public TextView timeView ;
        public ImageView ivMagicIcon;

        public SendMagicIconViewHolder(View itemView) {
            super(itemView);
            llayout = (LinearLayout)itemView.findViewById(R.id.chat_sound);
            pbDownload = (MaterialProgressBar) itemView.findViewById(R.id.pbDownload);
            btnError = (ImageButton) itemView.findViewById(R.id.btnError);
            timeView = (TextView) itemView.findViewById(R.id.chat_sound_time);
            ivMagicIcon = (ImageView)itemView.findViewById(R.id.ivMagicIcon);
        }
    }

    //通知样式
    public class NotifyTypeViewHolder extends MessageViewHolder {
        public TextView tvDesc;

        public NotifyTypeViewHolder(View itemView) {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        }
    }

    //分组时间描述
    public class SortedTimeTypeViewHolder extends MessageViewHolder {
        public TextView tvMessage;

        public SortedTimeTypeViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        }
    }

    //-------------------------ViewHolder数据模型 end--------------------------
}