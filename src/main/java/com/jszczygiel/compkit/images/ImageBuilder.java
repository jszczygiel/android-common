package com.jszczygiel.compkit.images;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jszczygiel.compkit.images.transformations.BlurTransformation;
import com.jszczygiel.compkit.images.transformations.PaddingTransformation;
import com.jszczygiel.compkit.images.transformations.RoundTransformation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class ImageBuilder {
  public static final float THREE_QUATERS_DENSITY = 0.75f;
  public static final float HALF_DENSITY = 0.5f;
  public static final float QUATER_DENSITY = 0.25f;
  private final Activity activity;
  private final Context context;
  private final Fragment fragment;
  private final List<Integer> transformations = new ArrayList<>();
  private ImageView imageView;
  private String url;
  private int resourceId;
  //OPTIONAL
  private int placeHolderId;
  private int animationId;
  private int errorPlaceHolderId;
  private ImageListener listener;
  private TargetListener target;
  private float sizeMultiplier = 1;
  private int width;
  private int height;
  private String key;
  private float padding;

  private ImageBuilder(Activity context) {
    this.activity = context;
    this.context = null;
    this.fragment = null;
  }

  private ImageBuilder(Context context) {
    this.context = context;
    this.fragment = null;
    this.activity = null;
  }

  private ImageBuilder(Fragment context) {
    this.fragment = context;
    this.context = null;
    this.activity = null;
  }

  public static ImageBuilder with(Activity activity) {
    return new ImageBuilder(activity);
  }

  public static ImageBuilder with(Context context) {
    return new ImageBuilder(context);
  }

  public static ImageBuilder with(Fragment fragment) {
    return new ImageBuilder(fragment);
  }

  public static void clear(View view) {
    Glide.clear(view);
  }

  /**
   * Each added transformation will be executed in order of which it was added
   *
   * @param flag Tranformation flag
   * @return instance of builder
   */
  public ImageBuilder transform(@Transformations.Type int flag) {
    transformations.add(flag);
    return this;
  }

  public ImageBuilder load(String url) {
    this.url = url;
    return this;
  }

  public ImageBuilder load(int resourceId) {
    this.resourceId = resourceId;
    return this;
  }

  public ImageBuilder listener(ImageListener listener) {
    this.listener = listener;
    return this;
  }

  public ImageBuilder into(ImageView view) {
    this.imageView = view;
    return this;
  }

  public ImageBuilder into(TargetListener target) {
    this.target = target;
    return this;
  }

  public ImageBuilder size(int width, int height) {
    this.width = width;
    this.height = height;
    return this;
  }

  public ImageBuilder padding(float padding) {
    this.padding = padding;
    return this;
  }

  public ImageBuilder key(String key) {
    this.key = key;
    return this;
  }

  public ImageBuilder placeHolder(int resoruceId) {
    this.placeHolderId = resoruceId;
    return this;
  }

  /**
   * Resizes loaded images for give sizeMultiplayer. Doesnt work if used with Transformation due to
   * glide bug
   */
  public ImageBuilder sizeMultiplier(float sizeMultiplier) {
    this.sizeMultiplier = sizeMultiplier;
    return this;
  }

  public ImageBuilder error(int errorPlaceHolderId) {
    this.errorPlaceHolderId = errorPlaceHolderId;
    return this;
  }

  public ImageBuilder animate(int animationId) {
    this.animationId = animationId;
    return this;
  }

  public Request build() {
    // Preconditions
    if (context == null && activity == null && fragment == null) {
      throw new IllegalArgumentException("either context or activity or fragment should be set");
    }

    if (placeHolderId <= 0 && TextUtils.isEmpty(url) && resourceId <= 0) {
      throw new IllegalArgumentException("nothing to load");
    }

    if (imageView == null && target == null) {
      throw new IllegalArgumentException("imageView or targer missing");
    }

    if (sizeMultiplier < 0f || sizeMultiplier > 1f) {
      throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
    }

    // building
    Context localContext;
    RequestManager item;
    if (context != null) {
      localContext = context;
      item = Glide.with(context);
    } else if (activity != null) {
      localContext = activity;
      item = Glide.with(activity);
    } else {
      localContext = fragment.getActivity();
      item = Glide.with(fragment);
    }

    if (!isValidContext(localContext)) {
      return null;
    }

    DrawableRequestBuilder request = item.load(url != null ? url.replace(" ", "") : resourceId);
    if (width != 0 && height != 0) {
      request = request.override(width, height);
    }
    if (animationId > 0) {
      request = request.animate(animationId);
    }
    if (placeHolderId > 0) {
      request = request.placeholder(placeHolderId);
    }
    if (errorPlaceHolderId > 0) {
      request = request.error(errorPlaceHolderId);
    }
    request.sizeMultiplier(sizeMultiplier);
    if (key == null) {
      key = url != null ? url : String.valueOf(resourceId);
    }
    if (listener != null) {
      request =
          request.listener(
              new RequestListener() {
                @Override
                public boolean onException(
                    Exception e, Object model, Target target, boolean isFirstResource) {
                  listener.onCancel();
                  return false;
                }

                @Override
                public boolean onResourceReady(
                    Object resource,
                    Object model,
                    Target target,
                    boolean isFromMemoryCache,
                    boolean isFirstResource) {
                  listener.onLoaded();
                  return false;
                }
              });
    }

    ArrayList<BitmapTransformation> transformationList = new ArrayList<>();

    for (Integer trans : transformations) {
      switch (trans) {
        case Transformations.ROUND:
          transformationList.add(new RoundTransformation(localContext, padding));
          break;
        case Transformations.BLUR:
          transformationList.add(new BlurTransformation(localContext));
          break;
        case Transformations.CENTER_CROP:
          request = request.centerCrop();
          break;
        case Transformations.FIT_CENTER:
          request = request.fitCenter();
          break;
      }
    }
    if (padding > 0
        && Observable.from(transformationList)
                .ofType(RoundTransformation.class)
                .count()
                .toBlocking()
                .first()
            == 0) {
      transformationList.add(new PaddingTransformation(localContext, padding));
    }

    if (transformationList.size() > 0) {
      request =
          request.transform(
              transformationList.toArray(new BitmapTransformation[transformationList.size()]));
    }

    if (request == null) {
      return null;
    }

    request.dontAnimate();

    if (imageView != null) {
      Target targetRequest = request.into(imageView);
      return new Request(targetRequest.getRequest(), key);
    }
    if (target != null) {
      SimpleTarget<Drawable> simpleTarget =
          new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, GlideAnimation glideAnimation) {
              target.onResourceReady(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
              target.onLoadFailed(e);
            }
          };
      request.into(simpleTarget);
      return new Request(simpleTarget.getRequest(), key);
    }
    return null;
  }

  private boolean isValidContext(@NonNull Context localContext) {
    if (localContext instanceof Activity) {
      return !((Activity) localContext).isFinishing() && !((Activity) localContext).isDestroyed();
    }
    return true;
  }

  public interface ImageListener {
    void onLoaded();

    void onCancel();
  }

  public static class Transformations {

    public static final int ROUND = 0;
    public static final int BLUR = 1;
    public static final int CENTER_CROP = 2;
    public static final int FIT_CENTER = 3;
    @Type int transformation;

    public Transformations(@Type int transformation) {
      this.transformation = transformation;
    }

    @Type
    public int getType() {
      return transformation;
    }

    @IntDef({ROUND, BLUR, FIT_CENTER, CENTER_CROP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}
  }

  public static class Request {

    private final com.bumptech.glide.request.Request request;
    private final String key;

    public Request(com.bumptech.glide.request.Request request, String key) {
      this.request = request;
      this.key = key;
    }

    public void cancel() {
      request.clear();
      request.recycle();
    }

    public String getKey() {
      return key;
    }
  }

  public abstract static class TargetListener {

    public void onResourceReady(Drawable bitmap) {}

    public void onLoadFailed(Exception exception) {}
  }
}
