/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.mobile.ui.login;


import android.util.Base64;

import org.apache.taverna.mobile.data.DataManager;
import org.apache.taverna.mobile.data.model.User;
import org.apache.taverna.mobile.ui.base.BasePresenter;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    public final String LOG_TAG = getClass().getSimpleName();

    private DataManager mDataManager;

    private Subscription mSubscriptions;

    public LoginPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
    }

    public void login(String username, String password, boolean flagLogin) {
        if (mSubscriptions != null) mSubscriptions.unsubscribe();

        getMvpView().showProgressDialog(true);

        mSubscriptions = mDataManager.getLoginUserDetail(getEncodedCredential(username, password)
                , flagLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showDashboardActivity();
                        getMvpView().showProgressDialog(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showCredentialError();
                        getMvpView().showProgressDialog(false);
                    }

                    @Override
                    public void onNext(User user) {

                    }
                });
    }

    private String getEncodedCredential(String username, String password) {

        return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64
                .NO_WRAP);
    }

}
