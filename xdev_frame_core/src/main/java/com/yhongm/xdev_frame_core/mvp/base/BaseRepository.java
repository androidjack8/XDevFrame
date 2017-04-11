package com.yhongm.xdev_frame_core.mvp.base;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yhongm.xdev_frame_core.custom_eventbus.EventBus;
import com.yhongm.xdev_frame_core.custom_agera.BaseObservable;
import com.yhongm.xdev_frame_core.custom_agera.Repositories;
import com.yhongm.xdev_frame_core.custom_agera.Repository;
import com.yhongm.xdev_frame_core.custom_agera.Result;
import com.yhongm.xdev_frame_core.custom_agera.Supplier;
import com.yhongm.xdev_frame_core.custom_agera.Updatable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yhongm on 2017/03/31.
 * 数据来源基类
 */

public class BaseRepository<D> extends BaseObservable implements Updatable, Supplier<Result<D>> {
    protected Context mContext;
    private boolean isAttach = false, isDetach = false;
    private Result<D> result;
    protected Repository<Result<D>> mRepository;

    public BaseRepository(Context context) {
        this.mContext = context;
        isAttach = false;
        isDetach = false;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        mRepository = Repositories.repositoryWithInitialValue(Result.<D>absent())
                .observe(this)
                .onUpdatesPerLoop()
                .goTo(executorService)
                .thenGetFrom(this)
                .compile();
    }

    public void attachView() {
        isAttach = true;
        mRepository.addUpdatable(this);
    }

    public void detachView() {
        isDetach = true;
        mRepository.removeUpdatable(this);
    }

    @Override
    protected void dispatchUpdate() {
        if (!isAttach) {
            throw new IllegalArgumentException("attachView and detachView Function must call");//attachView()和detachView方法必须被调用;
        }
        super.dispatchUpdate();
    }

    @Override
    public void update() {
        result = mRepository.get();
        EventBus.getDefault().post(this);
    }

    public Repository<Result<D>> getRepository() {
        return mRepository;
    }

    @NonNull
    @Override
    public Result<D> get() {
        return null;
    }

    public Result<D> getResult() {
        return result;
    }
}
