package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.zslide.R;

import java.util.HashMap;

/**
 * Created by chulwoo on 16. 3. 10..
 */
public class UpButton extends ImageButton implements View.OnClickListener {

    private Animation scaleUpAnimation = new ScaleUpAnimation();
    private Animation scaleDownAnimation = new ScaleDownAnimation();
    private Behavior behavior;

    public UpButton(Context context) {
        super(context);
        init(context);
    }

    public UpButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UpButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UpButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context) {
        setImageResource(R.drawable.ic_up);
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        behavior.up();
    }

    /**
     * 애니메이션과 함께 UpButton을 보여줌.
     * show, hide 메소드의 애니메이션이 동작중일 때에는 동작하지 않음
     */
    public void show() {
        startAnimation(scaleUpAnimation);
    }

    /**
     * 애니메이션과 함께 UpButton을 가림.
     * show, hide 메소드의 애니메이션이 동작중일 때에는 동작하지 않음
     */
    public void hide() {
        startAnimation(scaleDownAnimation);
    }

    public void setBehaviorTarget(View target) {
        this.behavior = BehaviorFactory.create(this, target);
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    public interface Behavior {
        void invalidate();

        void up();
    }

    public interface UsableUpButton {
        View getScrollableView();
    }

    public static class BehaviorFactory {
        public static Behavior create(UpButton upButton, View view) {
            Behavior behavior;
            if (view instanceof RecyclerView) {
                behavior = new RecyclerViewBehavior(upButton, (RecyclerView) view);
            } else if (view instanceof BaseWebView) {
                behavior = new BaseWebViewBehavior(upButton, (BaseWebView) view);
            } else if (view instanceof BaseScrollView) {
                behavior = new BaseScrollViewBehavior(upButton, (BaseScrollView) view);
            } else if (view instanceof NestedScrollView) {
                behavior = new NestedScrollViewBehavior(upButton, (NestedScrollView) view);
            } else {
                throw new IllegalArgumentException(view.getClass().getSimpleName() + "is not supported.");
            }

            return behavior;
        }
    }

    public static class RecyclerViewBehavior extends RecyclerView.OnScrollListener implements Behavior {

        private RecyclerView target;
        private BaseBehavior behavior;
        private int scrolledY = 0;

        public RecyclerViewBehavior(UpButton upButton, RecyclerView target) {
            this.target = target;
            this.target.addOnScrollListener(this);
            this.behavior = new BaseBehavior(upButton) {
                @Override
                public void up() {
                    RecyclerViewBehavior.this.up();
                }
            };
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrolledY += dy;
            behavior.onScrolled(scrolledY);
        }

        @Override
        public void up() {
            target.smoothScrollToPosition(0);
        }

        @Override
        public void invalidate() {
            behavior.invalidate();
        }
    }

    public static class NestedScrollViewBehavior extends BaseBehavior implements NestedScrollView.OnScrollChangeListener {

        private NestedScrollView target;

        public NestedScrollViewBehavior(UpButton upButton, NestedScrollView target) {
            super(upButton);
            this.target = target;
            this.target.setOnScrollChangeListener(this);
        }

        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            onScrolled(scrollY);
        }

        @Override
        public void up() {
            target.smoothScrollTo(0, 0);
        }
    }

    public static class BaseWebViewBehavior extends BaseBehavior implements BaseWebView.OnScrollListener {

        private BaseWebView target;

        public BaseWebViewBehavior(UpButton upButton, BaseWebView target) {
            super(upButton);
            this.target = target;
            this.target.setOnScrollListener(this);
        }

        @Override
        public void onScrolled(int l, int t, int oldl, int oldt) {
            onScrolled(t);
        }

        @Override
        public void up() {
            this.target.scrollTo(0, 0);
        }
    }

    public static class BaseScrollViewBehavior extends BaseBehavior implements BaseScrollView.OnScrollListener {

        private BaseScrollView target;

        public BaseScrollViewBehavior(UpButton upButton, BaseScrollView target) {
            super(upButton);
            this.target = target;
            this.target.setOnScrollListener(this);
        }

        @Override
        public void onScrolled(int l, int t, int oldl, int oldt) {
            onScrolled(t);
        }

        @Override
        public void up() {
            this.target.smoothScrollTo(0, 0);
        }
    }

    public abstract static class BaseBehavior implements Behavior {

        private int offset = 100;
        private int thresholdY = 2000;
        private boolean isShown = false;

        private UpButton upButton;

        public BaseBehavior(UpButton upButton) {
            this.upButton = upButton;
            invalidate();
        }

        @Override
        public void invalidate() {
            upButton.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
        }

        public void setThresholdY(int thresholdY) {
            this.thresholdY = thresholdY;
        }

        public void onScrolled(int position) {
            if (position > (thresholdY + offset) && !isShown) {
                upButton.show();
                isShown = true;
            } else if (position <= (thresholdY - offset) && isShown) {
                upButton.hide();
                isShown = false;
            }
        }
    }

    public static class UpButtonBehaviorMixer {

        protected UpButton upButton;

        private HashMap<Integer, Behavior> behaviors;

        public UpButtonBehaviorMixer(UpButton upButton) {
            this.upButton = upButton;
            behaviors = new HashMap<>();
        }

        protected static int getKey(View view) {
            if (view == null) {
                return 0;
            }
            return view.hashCode();
        }

        @Nullable
        public Behavior getBehavior(View view) {
            return behaviors.get(getKey(view));
        }

        public void setActiveView(View view) {
            UpButton.Behavior behavior = getBehavior(view);

            if (behavior == null || view == null) {
                upButton.setVisibility(View.GONE);
                return;
            }

            behavior.invalidate();
            upButton.setBehavior(behavior);
        }

        public void addView(@NonNull View view) {
            behaviors.put(getKey(view), UpButton.BehaviorFactory.create(upButton, view));
        }
    }

    public static class SimplePagerUpButtonBehaviorMixer extends ViewPager.SimpleOnPageChangeListener {

        protected UpButtonBehaviorMixer mixer;
        protected Fragment[] fragments;

        public SimplePagerUpButtonBehaviorMixer(UpButton upButton, Fragment[] fragments) {
            mixer = new UpButtonBehaviorMixer(upButton);
            this.fragments = fragments;
            onPageSelected(0);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Fragment fragment = fragments[position];

            if (fragment instanceof UsableUpButton) {
                View target = ((UsableUpButton) fragment).getScrollableView();

                if (target == null) {
                    return;
                }

                if (mixer.getBehavior(target) == null) {
                    mixer.addView(target);
                }
                mixer.setActiveView(target);
            } else {
                mixer.setActiveView(null);
            }
        }
    }

    class SimpleAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class ScaleUpAnimation extends ScaleAnimation {

        public ScaleUpAnimation() {
            super(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            setDuration(500);
            setInterpolator(new BounceInterpolator());
//            setInterpolator(new AccelerateInterpolator(1.3f));
            setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private class ScaleDownAnimation extends ScaleAnimation {

        public ScaleDownAnimation() {
            super(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            setDuration(300);
            setInterpolator(new DecelerateInterpolator(1.3f));
            setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}