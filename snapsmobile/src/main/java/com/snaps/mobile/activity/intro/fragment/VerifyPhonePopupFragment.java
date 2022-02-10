package com.snaps.mobile.activity.intro.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.data.event.member_verify.SnapsMemberVerifyEventInfo;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by kimduckwon on 2017. 7. 21..
 */

public class VerifyPhonePopupFragment extends VerifyPhoneFragment {
    private static final String TAG = VerifyPhonePopupFragment.class.getSimpleName();

    private static final float BANNER_IMAGE_HIGHT_RATIO = 0.54f;

    public VerifyPhonePopupFragment(){};

    public static VerifyPhonePopupFragment newInstance(ISnapsHamburgerMenuListener listener) {
        VerifyPhonePopupFragment fragment = new VerifyPhonePopupFragment();
        fragment.setMenuClickListener(listener);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

//        setPopupLayout();

        if(closeBtn != null) {
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getMenuClickListener() != null) {
                        if (TextUtils.isEmpty(userNo)) {
                            getMenuClickListener().onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_LOG_IN);
                        } else {
                            getMenuClickListener().onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
                        }
                    }
                }
            });
        }
        getMemberVerifyEventInfo();
        return v;
    }

    @Override
    public void completedVerify() {
        if (!isNewMember) {
            super.completedVerify();
            return;
        }

        boolean login= SnapsLoginManager.isLogOn(getContext());
        if (getMenuClickListener() != null) {
            if(login){
                getMenuClickListener().onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_COMPLETED_VERIFY);
            }else{
                getMenuClickListener().onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_VERIFI);
            }
        }
    }

    @Override
    public void setVerifyNumberResultType(final String type,final String msg) {
        if (!isNewMember) {
            super.setVerifyNumberResultType(type, msg);
            return;
        }

        switch (type){
            case VERIFY_NUMBER_RESULT_NOTKEY:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.is_not_valid_verify_number), null);
                break;
            case VERIFY_NUMBER_RESULT_EXISTENCENUMBER:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_existencenumber), null);
                break;
            case VERIFY_NUMBER_RESULT_DEVICE:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_device), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
            case VERIFY_NUMBER_RESULT_TERMINATION :
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_termination), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
            case VERIFY_NUMBER_RESULT_EXISTENCEUSER:
                String msgStr="";
                try{
                    msgStr=String.format(getActivity().getString(R.string.certification_existenceuser),msg);
                }catch (Exception e){
                    Dlog.e(TAG, e);
                    msgStr=getActivity().getString(R.string.certification_existenceuser);
                }
                MessageUtil.alertnoTitleOneBtn(getActivity(),msgStr,new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
            case VERIFY_NUMBER_RESULT_AUTHUSER:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_authuser), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
            case VERIFY_NUMBER_RESULT_OLDUSER:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_olduser), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
            case VERIFY_NUMBER_RESULT_SUCCESS:
                MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_push_coupon), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        completedVerify();
                    }
                });
                break;
        }
    }

    //팝업용 레이아웃 구현
    private void setPopupLayout() {
        try {
            closeBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.GONE);
            titleText.setText(getResources().getString(R.string.member_popup_certification_));
            eventImage.setVisibility(View.VISIBLE);
            eventImage.getLayoutParams().height = getEventImgHeight(UIUtil.getScreenWidth(getContext()));
            eventImage.requestLayout();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private int getEventImgHeight(int width){
        return ((int)(width * BANNER_IMAGE_HIGHT_RATIO));
    }

    private void getMemberVerifyEventInfo() {
        ATask.executeVoidWithThreadPoolBooleanDefProgress(getActivity(), new ATask.OnTaskResult() {
            SnapsMemberVerifyEventInfo snapsMemberVerifyEventInfo = null;
            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                snapsMemberVerifyEventInfo = HttpReq.verifyBannerGetImage(SnapsLoginManager.getUUserNo(getActivity()), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                return snapsMemberVerifyEventInfo != null && snapsMemberVerifyEventInfo.shouldShowCouponUI();
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    setNewMember(true);

                    setPopupLayout();

                    updateUIWithMemberVerifyEventInfo(snapsMemberVerifyEventInfo);
                } else {
                    setNewMember(false);
                }
            }
        });
    }

    private void updateUIWithMemberVerifyEventInfo(SnapsMemberVerifyEventInfo verifyEventInfo) {
        if (verifyEventInfo == null) return;

        try {
            loadBannerImage(verifyEventInfo.getAuthPopImage());

            loadCouponSelectUI(verifyEventInfo);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private void loadBannerImage(String imageUrl) {
        if (StringUtil.isEmpty(imageUrl) || eventImage == null) return;
        ImageLoader.with(getContext()).load(imageUrl).centerCrop().into(eventImage);
    }

    private void loadCouponSelectUI(SnapsMemberVerifyEventInfo verifyEventInfo) {
        if (!isValidCouponCount(verifyEventInfo)) return; //쿠폰이 최소한 2개 이상이어야 한다.

        if (couponSelectLayout != null)
            couponSelectLayout.setVisibility(View.VISIBLE);

        if (!StringUtil.isEmpty(verifyEventInfo.getTitle()) && tvCouponTitle != null) {
            tvCouponTitle.setText(verifyEventInfo.getTitle());
        }

        Map<String, String> coupons = verifyEventInfo.getCoupons();
        if (coupons == null) return;

        Set<String> keys = coupons.keySet();
        int index = 0;
        for (String key : keys) {
            if (key == null) continue;

            index++;

            if (index == 1) {
                setCouponSelector(couponSelectLayout01, checkBoxCoupon01, tvCouponName01, key, coupons.get(key));
            } else if (index == 2) {
                setCouponSelector(couponSelectLayout02, checkBoxCoupon02, tvCouponName02, key, coupons.get(key));
            } else if (index == 3) {
                setCouponSelector(couponSelectLayout03, checkBoxCoupon03, tvCouponName03, key, coupons.get(key));
            } else {
                break;
            }
        }
    }

    private void setCouponSelector(View layout, ImageView checkBox, TextView nameView, String key, String name) {
        if (layout != null)
            layout.setVisibility(View.VISIBLE);

        if (checkBox != null) {
            checkBox.setTag(key);
        }

        if (nameView != null)
            nameView.setText(name);
    }

    private boolean isValidCouponCount(SnapsMemberVerifyEventInfo verifyEventInfo) {
        if (verifyEventInfo == null || verifyEventInfo.getCoupons() == null) return false;

        LinkedHashMap<String, String> coupons = verifyEventInfo.getCoupons();
        Set<String> keys = coupons.keySet();
        if (keys.size() == 1) {
            setCouponCode(keys.iterator().next());
          return false;
        }
        return keys.size() >= 2;
    }
}
