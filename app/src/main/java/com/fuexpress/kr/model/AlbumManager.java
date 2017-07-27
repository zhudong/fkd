package com.fuexpress.kr.model;

import android.util.Log;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.SimpleAlbumBean;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fksproto.CsAlbum;
import fksproto.CsBase;

/**
 * Created by longer on 2017/7/7.
 */

public class AlbumManager {

    private final static String TAG = "AlbumManager";
    private static boolean isGettingList = false;
    public List<SimpleAlbumBean> simpleAlbumBeans;
    public List<ItemBean> mAlbumItemsList;
    public boolean mHasMore = false;

    //写一个单例
    private static AlbumManager ourInstance = new AlbumManager();

    public void logout() {
        simpleAlbumBeans = null;
    }

    public static AlbumManager getInstance() {
        return ourInstance;
    }

    private AlbumManager() {
    }

    public /*synchronized*/ void getSimpleAlbumList() {
        simpleAlbumBeans = null;
        if (isGettingList) {
            return;
        }
        isGettingList = true;
        getList(1);
    }

    public void addSimpleAlbumBean(SimpleAlbumBean simpleAlbumBean) {
        if (simpleAlbumBean != null && simpleAlbumBeans != null) {
            simpleAlbumBeans.add(0, simpleAlbumBean);
        }
    }

    public void addSimpleAlbumBean(long id, String title) {
        if (simpleAlbumBeans != null) {
            simpleAlbumBeans.add(0, new SimpleAlbumBean(id, title));
        }
    }

    public void deleteSimpleAlbumBean(long id) {
        if (simpleAlbumBeans != null) {
            for (SimpleAlbumBean simpleAlbumBean : simpleAlbumBeans) {
                if (simpleAlbumBean.album_id == id) {
                    simpleAlbumBeans.remove(simpleAlbumBean);
                    break;
                }
            }
        }
    }

    public void editSimpleAlbumBean(long id, String title) {
        if (simpleAlbumBeans != null) {
            for (SimpleAlbumBean simpleAlbumBean : simpleAlbumBeans) {
                if (simpleAlbumBean.album_id == id) {
                    simpleAlbumBean.setTitle(title);
                    break;
                }
            }
        }
    }

    private void getList(final int index) {
        final CsAlbum.GetAlbumListRequest.Builder builder = CsAlbum.GetAlbumListRequest.newBuilder();
        builder.setPageno(index);
        builder.setAuthor(AccountManager.getInstance().mUin);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setCurrencyid(AccountManager.getInstance().getCurrencyId());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsAlbum.GetAlbumListResponse>() {
            @Override
            public void onSuccess(CsAlbum.GetAlbumListResponse response) {
                List<CsBase.Album> albums = response.getAlbumsList();
                if (simpleAlbumBeans == null) {
                    simpleAlbumBeans = new ArrayList<>();
                }
                for (CsBase.Album album : albums) {
                    simpleAlbumBeans.add(new SimpleAlbumBean(album.getAlbumId(), album.getTitle()));
                }
                Log.i(TAG, "simpleAlbumBeans.size = " + simpleAlbumBeans.size());
                if (response.getMore()) {
                    getList(index + 1);
                } else {
                    isGettingList = false;
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_SIMPLE_ALBUM_SUCCESS, null));
                }

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.d(TAG, errMsg);
            }
        });
    }

    public void getSimpleAlbumListDatas(final int index) {
        final CsAlbum.GetAlbumListRequest.Builder builder = CsAlbum.GetAlbumListRequest.newBuilder();
        builder.setPageno(index);
        builder.setAuthor(AccountManager.getInstance().mUin);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setCurrencyid(AccountManager.getInstance().getCurrencyId());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setPagesize(20);
        NetEngine.postRequest(builder, new INetEngineListener<CsAlbum.GetAlbumListResponse>() {
            @Override
            public void onSuccess(CsAlbum.GetAlbumListResponse response) {
                List<CsBase.Album> albums = response.getAlbumsList();
                if (simpleAlbumBeans == null || index == 1) {
                    simpleAlbumBeans = new ArrayList<>();
                }
                for (CsBase.Album album : albums) {
                    simpleAlbumBeans.add(new SimpleAlbumBean(album.getAlbumId(), album.getTitle()));
                }
                Log.i(TAG, "simpleAlbumBeans.size = " + simpleAlbumBeans.size());
                EventBus.getDefault().post(new BusEvent(BusEvent.GET_SIMPLE_ALBUM_SUCCESS, response.getMore()));

            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.d(TAG, errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.REQUEST_FAIL, ret + errMsg));
            }
        });
    }


    /**
     * 这是请求图集元素的方法:
     *
     * @param albumId 图集的id
     * @param pageNum 第几页
     * @param status  状态(0为刷新or初始,1为加载更多)
     */
    public void getAlbumElementListRequest(long albumId, int pageNum, final int status) {
/*

        CsAlbum.GetAlbumElementListRequest.Builder builder = CsAlbum.GetAlbumElementListRequest.newBuilder();
        builder.setPageno(pageNum);
        builder.setAlbumId(albumId);
        if (mAlbumItemsList == null) {
            mAlbumItemsList = new ArrayList<>();
        }
        if (0 == status && mAlbumItemsList != null) {
            mAlbumItemsList.removeAll(mAlbumItemsList);
        }

        NetEngine.postRequest(builder, new INetEngineListener<CsAlbum.GetAlbumElementListResponse>() {

            @Override
            public void onSuccess(CsAlbum.GetAlbumElementListResponse response) {
                mAlbumItemsList.addAll(ClassUtil.convertItemList2ItemBean(response.getItemsList()));
                //mHasMore = response.getMore();
                boolean more = response.getMore();
                //发送even bus来提醒已经加载完成了:
                if (status == 1) {
                    //说明是加载更多的状态:
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_ALBUM_ITEMS_SUCCESS_MORE, more));
                } else if (0 == status) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.GET_ALBUM_ITEMS_SUCCESS, more));
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.e(TAG, errMsg);
            }
        });
*/

        //return mHasMore;
    }

    /**
     * 把Item加入到图集的方法
     *
     * @param itemId    item的id
     * @param itemTitle item的标题
     * @param albumId   图集的id
     */
    public void addItemToAlbumRequest(long itemId, String itemTitle, final long albumId, final String imageUrl) {
        //开始进行网络请求:
        CsAlbum.AddAlbumElementRequest.Builder builder = CsAlbum.AddAlbumElementRequest.newBuilder();

        builder.setAlbumId(albumId);

        builder.setElementId(itemId);

        builder.setElementTitle(itemTitle);

        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        NetEngine.postRequest(builder, new INetEngineListener<CsAlbum.AddAlbumElementResponse>() {

            @Override
            public void onSuccess(CsAlbum.AddAlbumElementResponse response) {
                //LogUtils.e("这是添加到图集的方法返回的成功:" + response.toString());
                EventBus.getDefault().post(new BusEvent(BusEvent.ADD_ITEM_TO_ALBUM_SUCCESS, albumId, imageUrl));
                //EventBus.getDefault().post(new BusEvent(BusEvent.USER_COUNT_UPDATE, null));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("这是添加到图集的方法返回的失败:" + errMsg);
                EventBus.getDefault().post(new BusEvent(BusEvent.ADD_ITEM_TO_ALBUM_FAIL, null));
            }
        });
    }
}
