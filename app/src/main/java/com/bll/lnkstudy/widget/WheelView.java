package com.bll.lnkstudy.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bll.lnkstudy.utils.DP2PX;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends ScrollView {

    private int displayItemCount,// 每页显示的数量

    viewWidth, initialY;

    private int selectedIndex = 1, itemHeight = 0, scrollDirection = -1,

    newCheck = 50, offset = OFF_SET_DEFAULT;

    private int[] selectedAreaBorder;

    private Context context;

    private LinearLayout views;

    private List<String> items;

    private Paint paint;

    private Runnable scrollerTask;

    private OnWheelViewListener onWheelViewListener;

    private static final int OFF_SET_DEFAULT = 1;

    private static final int SCROLL_DIRECTION_UP = 0;

    private static final int SCROLL_DIRECTION_DOWN = 1;



    public WheelView(Context context) {

        super(context);

        init(context);

    }

    public WheelView(Context context, AttributeSet attrs) {

        super(context, attrs);

        init(context);

    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        init(context);

    }

    //设置数据
    public void setItems(List<Integer> list) {
        items=new ArrayList<>();
        items.add("");
        items.add("");
        for (int i = 0; i < list.size(); i++) {
            items.add(list.get(i) +"");
        }
        items.add("");
        items.add("");
        initData();
    }

    //设置偏移个数
    public void setOffset(int offset) {

        this.offset = offset;

    }

    //设置当前位置
    public void setSelection(int position) {

        selectedIndex = position + offset;

        this.post(new Runnable() {

            @Override

            public void run() {

                WheelView.this.smoothScrollTo(0, position * itemHeight);

            }

        });

    }


    private void init(Context context) {

        this.context = context;

        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);

        views.setOrientation(LinearLayout.VERTICAL);

        this.addView(views);

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();

                if (initialY - newY == 0) {

                    final int remainder = initialY % itemHeight;

                    final int divided = initialY / itemHeight;

                    if (remainder == 0) {

                        selectedIndex = divided + offset;

                        onSelectorListener();

                    } else {

                        if (remainder > itemHeight / 2) {

                            WheelView.this.post(new Runnable() {

                                @Override

                                public void run() {

                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);

                                    selectedIndex = divided + offset + 1;

                                    onSelectorListener();

                                }

                            });

                        } else {

                            WheelView.this.post(new Runnable() {

                                @Override

                                public void run() {

                                    WheelView.this.smoothScrollTo(0, initialY - remainder);

                                    selectedIndex = divided + offset;

                                    onSelectorListener();

                                }

                            });

                        }

                    }

                } else {

                    initialY = getScrollY();

                    WheelView.this.postDelayed(scrollerTask, newCheck);

                }

            }

        };

    }

    public void startScrollerTask() {

        initialY = getScrollY();

        this.postDelayed(scrollerTask, newCheck);

    }

    private void initData() {

        displayItemCount = offset * 2 + 1;

        for (int i = 0; i < items.size(); i++) {
            views.addView(createView(i,items.get(i)));
        }
        refreshItemView(0);

    }

    private TextView createView(int i,String item) {

        TextView tv = new TextView(context);

        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DP2PX.dip2px(context,40f)));
        tv.setSingleLine(true);
        tv.setTextSize(19f);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=tv.getText().toString();
                if (!TextUtils.isEmpty(s)){
                    onWheelViewListener.onClick(tv.getText().toString());
                    setSelection(i-2);
                }
            }
        });

        if (0 == itemHeight) {

            itemHeight = DP2PX.dip2px(context,40f);

            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();

            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));

        }

        return tv;

    }

    @Override

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        refreshItemView(t);

        if (t > oldt) {

            scrollDirection = SCROLL_DIRECTION_DOWN;

        } else {

            scrollDirection = SCROLL_DIRECTION_UP;

        }

    }

    private void refreshItemView(int y) {

        int position = y / itemHeight + offset;

        int remainder = y % itemHeight;

        int divided = y / itemHeight;

        if (remainder == 0) {

            position = divided + offset;

        } else {

            if (remainder > itemHeight / 2) {

                position = divided + offset + 1;

            }

        }

        int childSize = views.getChildCount();

        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }

            if (position == i) {
                itemView.setTextColor(Color.parseColor("#000000"));
                itemView.setTextSize(25f);
            } else {
                itemView.setTextColor(Color.parseColor("#000000"));
                itemView.setTextSize(19f);
            }

        }

    }

    private int[] obtainSelectedAreaBorder() {

        if (null == selectedAreaBorder) {

            selectedAreaBorder = new int[2];

            selectedAreaBorder[0] = itemHeight * offset;

            selectedAreaBorder[1] = itemHeight * (offset + 1);

        }
        return selectedAreaBorder;

    }

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }
        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#d8d8d8"));
            paint.setStrokeWidth(2);
        }
        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
            }
            @Override
            public void setAlpha(int alpha) {
            }
            @Override
            public void setColorFilter(ColorFilter cf) {
            }
            @SuppressLint("WrongConstant")
            @Override
            public int getOpacity() {
                return 0;
            }

        };
        super.setBackgroundDrawable(background);
    }



    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;

        setBackgroundDrawable(null);

    }

    /**
     * 选中回调
     */

    private void onSelectorListener() {

        if (null != onWheelViewListener) {

            onWheelViewListener.onSelector(selectedIndex-offset, items.get(selectedIndex));

        }

    }



    @Override

    public void fling(int velocityY) {

        super.fling(velocityY / 3);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();

        }

        return super.onTouchEvent(ev);

    }


    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {

        this.onWheelViewListener = onWheelViewListener;

    }

    public interface OnWheelViewListener {
        void onSelector(int selectedIndex, String item) ;
        void onClick(String item) ;
    }


    private int getViewMeasuredHeight(View view) {

        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        view.measure(width, expandSpec);

        return view.getMeasuredHeight();

    }

}
