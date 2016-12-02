package com.wuzch.mymenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/11/30.
 */

public class MainUI extends RelativeLayout {
    private Context context;
    private FrameLayout leftMenu;
    private FrameLayout middleMenu;
    private FrameLayout middleMask;
    private FrameLayout rightMenu;
    private Scroller mScroller;
    /**
     * 为三个自定义layout设置各自的id
     */
    public static final int LEFT_MENU_ID=111111;
    public static final int MIDDLE_MENU_ID=222222;
    public static final int RIGHT_MENU_ID=333333;

    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context=context;
        //第一个参数是context，第二个参数是渲染器
        mScroller=new Scroller(context,new DecelerateInterpolator());
        leftMenu=new FrameLayout(context);
        leftMenu.setBackgroundColor(Color.MAGENTA);
        leftMenu.setId(LEFT_MENU_ID);
        middleMenu=new FrameLayout(context);
        middleMenu.setBackgroundColor(Color.GREEN);
        middleMenu.setId(MIDDLE_MENU_ID);
        rightMenu=new FrameLayout(context);
        rightMenu.setBackgroundColor(Color.GRAY);
        rightMenu.setId(RIGHT_MENU_ID);

        middleMask=new FrameLayout(context);
        middleMask.setBackgroundColor(R.color.colorDim);
        //将三个layout填充到最外面的layout中
        addView(leftMenu);
        addView(middleMenu);
        addView(rightMenu);
        addView(middleMask);
        middleMask.setAlpha(0);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        int curX= Math.abs(getScrollX());
        float scale=curX/(float) leftMenu.getMeasuredWidth();
        middleMask.setAlpha(scale);
    }

    /**
     * 测量高度和宽度
     * @param widthMeasureSpec  屏幕的宽度
     * @param heightMeasureSpec 屏幕的高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置中间memu的高度和宽度
        middleMenu.measure(widthMeasureSpec,heightMeasureSpec);
        //设置中间模糊板块
        middleMask.measure(widthMeasureSpec, heightMeasureSpec);
        //获取整体屏幕宽度
        int realWidth=MeasureSpec.getSize(widthMeasureSpec);
        //这个宽度就是整个屏幕的80%
        //第一个参数是当前屏幕宽度，第二个参数什么方式进行测量,以精准的方式测量
        int tempWidthMeasure=MeasureSpec.makeMeasureSpec((int)(realWidth*0.8f),MeasureSpec.EXACTLY);
        leftMenu.measure(tempWidthMeasure,heightMeasureSpec);
        rightMenu.measure(tempWidthMeasure,heightMeasureSpec);
    }

    /**
     * 将之前创建的layout填充进去,参数都为整个屏幕大小
     * @param changed   改变监听
     * @param l 左边
     * @param t 顶部
     * @param r 右边
     * @param b 底部
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //填充整个屏幕
        middleMenu.layout(l,t,r,b);
        //模糊板块与中间的layout重合
        middleMask.layout(l, t, r, b);
        //在middleMenu的左边
        leftMenu.layout(l-leftMenu.getMeasuredWidth(),t,r,b);
        //在middleMenu的右边,右边界的位置是middleMenu的左边界开始计算
        rightMenu.layout(l+middleMenu.getMeasuredWidth(),t,r+middleMenu.getMeasuredWidth()+rightMenu.getMeasuredWidth(),b);
    }

    /**
     * 菜单的左右滑动
     */
    //事件的分发
    private boolean isTestCompete=false;    //便是是否进入测试的值
    private boolean isLeftRightEvent;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isTestCompete){
            getEventType(ev);
            return true;
        }
        if (isLeftRightEvent){
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    int curScrollX=getScrollX();//获取滑动距离
                    int dis_x=(int) ev.getX()-point.x;//获取手指放下和滑动之间的距离
                    int expectX=-dis_x+curScrollX;//两个值都是同一方向
                    int finalX=0;
                    if (expectX<0){
                        //小于0则为向左滑动
                        //返回这两个值较大的那个
                        finalX=Math.max(expectX,-leftMenu.getMeasuredWidth());
                    }else {
                        //返回两个值较小的那个
                        finalX=Math.min(expectX,rightMenu.getMeasuredWidth());
                    }
                    //滑动到指定位置
                    scrollTo(finalX,0);
                    //滑动完之后还需要给触点值赋值
                    point.x=(int)ev.getX();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    curScrollX=getScrollX();
                    //当滑动距离大于一半时，自动滑动到左边菜单，否则回到原处
                    if (Math.abs(curScrollX)>=leftMenu.getMeasuredWidth()>>1){
                        if (curScrollX<0){
                            //参数，起始的X，起始的Y，终止的X，终止的Y,还有时长可以设置
                            mScroller.startScroll(curScrollX,0,
                                    -leftMenu.getMeasuredWidth()-curScrollX,0,200);
                        }else {
                            mScroller.startScroll(curScrollX,0,
                                    leftMenu.getMeasuredWidth()-curScrollX,0,200);
                        }
                    }else {
                        //返回到起始位置
                        mScroller.startScroll(curScrollX,0,-curScrollX,0,200);
                    }
                    //以上操作需要重绘view。否则无动画效果,同时也需要回调computeScroll()方法
                    invalidate();
                    //左右滑动完成后，需要重写初始化isTestCompete的值，以便可以进行下一次触摸动作
                    isTestCompete=false;
                    isLeftRightEvent=false;
                    break;
            }
        }else {
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    //当上下滑动抬起时，isTestCompete和isLeftRightEvent都是true，所以需要重新对其false赋值。
                    isTestCompete=false;
                    isLeftRightEvent=false;
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * mScroller回调
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!mScroller.computeScrollOffset()){
            return;
        }
        //获取滑动的距离
        int tempX=mScroller.getCurrX();
        scrollTo(tempX,0);
    }

    private Point point=new Point();//获取点击的位置坐标，以便进行操作
    private static final int TEST_MOVE=20;  //用来对比滑动距离是否大于这个值，超过则为滑动
    private void getEventType(MotionEvent ev) {
        //基本事件三个,按下down，移动move，抬起up，触摸边缘cancel
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                point.x=(int)ev.getX();
                point.y=(int)ev.getY();
                //抛给系统处理事件机制
                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                //给定一个值，超过这个值才判断为滑动，比如是20px
                int disX=Math.abs((int)ev.getX()-point.x);//取绝对值
                int disY=Math.abs((int)ev.getY()-point.y);//取绝对值
                if (disX>TEST_MOVE&&disX>disY){
                    //左右滑动
                    isLeftRightEvent=true;
                    isTestCompete=true;
                    //滑动完后还是需要获取触点的坐标
                    point.x=(int)ev.getX();
                }else if (disY>TEST_MOVE&&disY>disX){
                    //上下滑动
                    isLeftRightEvent=false;
                    isTestCompete=true;
                    point.x=(int)ev.getX();
                    point.y=(int)ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //抛给系统处理事件机制
                super.dispatchTouchEvent(ev);
                isLeftRightEvent=false;
                isTestCompete=false;
                break;
        }
    }
}
