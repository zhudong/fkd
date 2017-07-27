package com.fuexpress.kr.ui.view.imageselector;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.view.imageselector.imsadapter.FolderAdapter;
import com.fuexpress.kr.ui.view.imageselector.imsadapter.ImageAdapter;
import com.fuexpress.kr.ui.view.imageselector.imsbean.Folder;
import com.fuexpress.kr.ui.view.imageselector.imsbean.Image;
import com.fuexpress.kr.ui.view.imageselector.imsutils.ImFileUtils;
import com.fuexpress.kr.ui.view.imageselector.imsutils.ImTimeUtils;
import com.fuexpress.kr.ui.view.imageselector.imsutils.ImUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 * @创建者 ${user}
 * @创时间 2016/7/8
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class ImageSelectorFragment extends Fragment {

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final int REQUEST_CAMERA = 100;

    private ArrayList<String> resultList = new ArrayList<>();
    private List<Folder> folderList = new ArrayList<>();

    private List<Image> imageList = new ArrayList<>();

    private Callback callback;

    private ImageAdapter imageAdapter;
    private FolderAdapter folderAdapter;

    private ListPopupWindow folderPopupWindow;

    private TextView time_text;
    private TextView category_button;
    private View popupAnchorView;
    private GridView grid_image;
    private boolean isFirstLoadFolder = true;


    private int gridWidth, gridHeight;

    private boolean hasFolderGened = false;

    private File tempFile;

    private Context context;

    private ImageConfig imageConfig;
    private TextView mTv_in_imageFragment;
    private TextView mRedPointTv;
    private ListView mLv_in_image_selector_fragment;
    private boolean isExpand;
    private RelativeLayout mRl_bg_in_imageSF;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement ImageSelectorFragment.Callback interface...");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.imageselector_main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        time_text = (TextView) view.findViewById(R.id.time_text);
        category_button = (TextView) view.findViewById(R.id.category_button);
        grid_image = (GridView) view.findViewById(R.id.grid_image);
        popupAnchorView = view.findViewById(R.id.footer_layout);
        mTv_in_imageFragment = (TextView) view.findViewById(R.id.tv_in_imageFragment);
        mLv_in_image_selector_fragment = (ListView) view.findViewById(R.id.lv_in_image_selector_fragment);
        mRl_bg_in_imageSF = (RelativeLayout) view.findViewById(R.id.rl_bg_in_imageSF);
        mRl_bg_in_imageSF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimMethod(false);
            }
        });
        //mRedPointTv = (TextView) view.findViewById(R.id.tv_red_point_in_title_bar);
        //mRedPointTv

        time_text.setVisibility(View.GONE);

        init();
    }


    public void showThePopUpWindow() {
        //getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
        /*if (folderPopupWindow == null) {
            createPopupFolderList(gridWidth, gridHeight);
        }

        if (folderPopupWindow.isShowing()) {
            folderPopupWindow.dismiss();
        } else {
            //folderAdapter.notifyDataSetChanged();
            folderPopupWindow.show();
            int index = folderAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            folderPopupWindow.getListView().setSelection(index);
        }*/
        mLv_in_image_selector_fragment.setAdapter(folderAdapter);
        mLv_in_image_selector_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                folderAdapter.setSelectIndex(position);

                final int index = position;
                final AdapterView v = parent;
                startAnimMethod(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //folderPopupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            //category_button.setText(R.string.all_folder);
                            ImageSelectorActivity activity = (ImageSelectorActivity) getActivity();
                            activity.setTitleText(getString(R.string.all_folder));
                            if (imageConfig.isShowCamera()) {
                                imageAdapter.setShowCamera(true);
                            } else {
                                imageAdapter.setShowCamera(true);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                imageList.clear();
                                imageList.addAll(folder.images);
                                imageAdapter.notifyDataSetChanged();

                                //category_button.setText(folder.name);
                                ImageSelectorActivity activity = (ImageSelectorActivity) getActivity();
                                activity.setTitleText(folder.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    imageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            imageAdapter.setShowCamera(true);
                        }

                        // 滑动到最初始位置
                        grid_image.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });
        mLv_in_image_selector_fragment.setVisibility(View.VISIBLE);
        grid_image.setEnabled(false);
        if (isExpand) {
            startAnimMethod(false);
        } else {
            startAnimMethod(true);
        }

    }

    private void startAnimMethod(boolean isOpen) {
        int height = ImUtils.dip2px(getActivity(), 300);
        int fromWhere = 0;
        int toPlace = 0;
        if (isOpen) {
            isExpand = true;
            fromWhere = 0;
            toPlace = height;
            mRl_bg_in_imageSF.setVisibility(View.VISIBLE);
        } else {
            isExpand = false;
            fromWhere = height;
            toPlace = 0;
            mRl_bg_in_imageSF.setVisibility(View.GONE);
            grid_image.setEnabled(true);
        }

        ValueAnimator animator = ValueAnimator.ofInt(fromWhere, toPlace);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = mLv_in_image_selector_fragment.getLayoutParams();
                int height = (int) animation.getAnimatedValue();
                params.height = height;
                mLv_in_image_selector_fragment.setLayoutParams(params);
                //mLlparent.updateViewLayout(userInfoLayout, params);
            }
        });
        /*animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        animator.start();
    }

    private void init() {
        imageConfig = ImageSelector.getImageConfig();

        folderAdapter = new FolderAdapter(context, imageConfig);

        imageAdapter = new ImageAdapter(context, imageList, imageConfig);

        imageAdapter.setShowCamera(imageConfig.isShowCamera());
        imageAdapter.setShowSelectIndicator(imageConfig.isMutiSelect());
        grid_image.setAdapter(imageAdapter);

        resultList = imageConfig.getPathList();

        category_button.setText(R.string.all_folder);
        category_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showThePopUpWindow();
            }
        });


        grid_image.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    time_text.setVisibility(View.GONE);
                } else if (scrollState == SCROLL_STATE_FLING) {
                    time_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (time_text.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                    Image image = (Image) view.getAdapter().getItem(index);
                    if (image != null) {
                        time_text.setText(ImTimeUtils.formatPhotoDate(image.path));
                    }
                }
            }
        });

        grid_image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = grid_image.getWidth();
                final int height = grid_image.getHeight();

                gridWidth = width;
                gridHeight = height;

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
                imageAdapter.setItemSize(columnWidth);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    grid_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    grid_image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        grid_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (imageAdapter.isShowCamera()) {
                    if (i == 0) {
                        checkAuthorAndShowCamara();

                    } else {
                        Image image = (Image) adapterView.getAdapter().getItem(i);
                        selectImageFromGrid(image, imageConfig.isMutiSelect());
                    }
                } else {
                    // 正常操作
                    Image image = (Image) adapterView.getAdapter().getItem(i);
                    selectImageFromGrid(image, imageConfig.isMutiSelect());
                }
            }
        });


    }


    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height) {
        folderPopupWindow = new ListPopupWindow(getActivity());
        folderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        folderPopupWindow.setAdapter(folderAdapter);
        folderPopupWindow.setContentWidth(width);
        folderPopupWindow.setWidth(width);
        folderPopupWindow.setHeight(height * 5 / 8);
        //folderPopupWindow.setAnchorView(popupAnchorView);
        folderPopupWindow.setAnchorView(mTv_in_imageFragment);
        folderPopupWindow.setModal(true);
        //folderPopupWindow.setDropDownGravity(Gravity.TOP);
        folderPopupWindow.setDropDownAlwaysVisible(true);
        //folderPopupWindow.setAnimationStyle(R.style.DiaLogBottom);
        folderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                folderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        folderPopupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            //category_button.setText(R.string.all_folder);
                            ImageSelectorActivity activity = (ImageSelectorActivity) getActivity();
                            activity.setTitleText(getString(R.string.all_folder));
                            if (imageConfig.isShowCamera()) {
                                imageAdapter.setShowCamera(true);
                            } else {
                                imageAdapter.setShowCamera(true);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                imageList.clear();
                                imageList.addAll(folder.images);
                                imageAdapter.notifyDataSetChanged();

                                //category_button.setText(folder.name);
                                ImageSelectorActivity activity = (ImageSelectorActivity) getActivity();
                                activity.setTitleText(folder.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    imageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            imageAdapter.setShowCamera(true);
                        }

                        // 滑动到最初始位置
                        grid_image.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (folderPopupWindow != null) {
            if (folderPopupWindow.isShowing()) {
                folderPopupWindow.dismiss();
            }
        }

        grid_image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int height = grid_image.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = grid_image.getWidth() / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (grid_image.getWidth() - columnSpace * (numCount - 1)) / numCount;
                imageAdapter.setItemSize(columnWidth);

                if (folderPopupWindow != null) {
                    folderPopupWindow.setHeight(height * 5 / 8);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    grid_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    grid_image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });


        super.onConfigurationChanged(newConfig);
    }

    /*适配6.0 权限检查*/
    @PermissionGrant(4)
    public void requestContactSuccess() {
        showCameraAction();
    }

    @PermissionDenied(4)
    public void requestContactFailed() {
        Toast.makeText(getActivity(), "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkAuthorAndShowCamara() {
        MPermissions.requestPermissions(ImageSelectorFragment.this, 4, Manifest.permission.CAMERA);
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            tempFile = ImFileUtils.createTmpFile(getActivity(), imageConfig.getFilePath());
            /*tempFile = new File(Environment.getExternalStorageDirectory(),
                    "Itemfor" + AccountManager.getInstance().mUin + System.currentTimeMillis() + ".jpg");*/
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(context, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImageFromGrid(Image image, boolean isMulti) {
        if (image != null) {
            if (isMulti) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if (callback != null) {
                        callback.onImageUnselected(image.path);
                    }
                } else {
                    if (imageConfig.getMaxSize() == resultList.size()) {
                        String msg = String.format(context.getString(R.string.msg_amount_limit_03), imageConfig.getMaxSize());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image.path);
                    if (callback != null) {
                        callback.onImageSelected(image.path);
                    }
                }
                imageAdapter.select(image);
            } else {
                if (callback != null) {
                    callback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempFile != null) {
                    if (callback != null) {
                        callback.onCameraShot(tempFile);
                    }
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader =
                        new CursorLoader(getActivity(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        tempImageList.add(image);
                        if (!hasFolderGened || !isFirstLoadFolder) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!folderList.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                folderList.add(folder);
                            } else {
                                Folder f = folderList.get(folderList.indexOf(folder));
                                f.images.add(image);
                                if (data.isFirst()) {
                                    f.cover = tempImageList.get(0);
                                }
                            }
                        }

                    } while (data.moveToNext());

                    imageList.clear();
                    imageList.addAll(tempImageList);
                    imageAdapter.notifyDataSetChanged();

                    if (resultList != null && resultList.size() > 0) {
                        imageAdapter.setDefaultSelected(resultList);
                    }

                    folderAdapter.setData(folderList, tempImageList.size());

                    hasFolderGened = true;
                    if (isFirstLoadFolder) {
                        isFirstLoadFolder = false;
                    }

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public interface Callback {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }

}
