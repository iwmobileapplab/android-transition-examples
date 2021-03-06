/*
 * Copyright 2018 Google LLC
 * Modified Copyright (C) 2018 Mobile Application Lab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.mobileapplab.library.gallery;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.mobileapplab.library.GalleryItem;
import net.mobileapplab.library.R;
import net.mobileapplab.library.view.MultiTouchImageView;
import net.mobileapplab.library.view.SwipeToDismissTouchListener;

/**
 * A fragment for displaying an image.
 */
public class ImageFragment extends Fragment {

    private static final String KEY_GALLERY_ITEM = ".key.galleryItem";

    public static ImageFragment newInstance(@NonNull GalleryItem item) {
        ImageFragment fragment = new ImageFragment();
        Bundle argument = new Bundle();
        argument.putParcelable(KEY_GALLERY_ITEM, item);
        fragment.setArguments(argument);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);

        Bundle arguments = getArguments();
        GalleryItem item = arguments.getParcelable(KEY_GALLERY_ITEM);
        final MultiTouchImageView imageView = view.findViewById(R.id.image);


        // Just like we do when binding views at the grid, we set the transition name to be the string
        // value of the image res.
        SwipeToDismissTouchListener listener = SwipeToDismissTouchListener.createFromView(imageView, new SwipeToDismissTouchListener.Callback() {
            @Override
            public void onDismiss() {
                Fragment pf = getParentFragment();
                if (pf != null && !pf.isRemoving() && pf.getActivity() != null && !pf.getActivity().isFinishing() && !pf.getActivity().isDestroyed()) {
                    pf.getActivity().onBackPressed();
                }
            }

            @Override
            public void onMove(float translationY) {

            }
        });
        imageView.setOnTouchListener(listener);
        imageView.setTransitionName(item.getTransitionName());

        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        Glide.with(this)
                .load(item.getUri())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                            target, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going in case of a failure.
                        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                imageView.isCompleteInitialPreDraw = true;
                                getParentFragment().startPostponedEnterTransition();
                                return true;
                            }
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going when the image is ready.
                        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                imageView.isCompleteInitialPreDraw = true;
                                getParentFragment().startPostponedEnterTransition();
                                return true;
                            }
                        });
                        return false;
                    }
                })
                .into((ImageView) view.findViewById(R.id.image));
        return view;
    }
}
