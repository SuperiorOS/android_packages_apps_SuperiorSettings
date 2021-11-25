package com.superior.settings.preferences;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.Utils;

public class BasePreferenceCategory extends PreferenceCategory {
    public BasePreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BasePreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BasePreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if (holder != null) {
            TextView title = (TextView) holder.findViewById(android.R.id.title);
            title.setTextColor(getContext().getResources().getColor(R.color.text_primary, null));
            LinearLayout rootLayout = (LinearLayout) title.getParent();
            rootLayout.setPadding(30, 0, 0, 0);

        }

    }
}
