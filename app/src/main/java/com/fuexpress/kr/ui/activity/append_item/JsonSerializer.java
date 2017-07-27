/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fuexpress.kr.ui.activity.append_item;

import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.bean.ItemAppendBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class JsonSerializer {

    private final Gson gson = new Gson();


    public String serialize(ItemAppendBean bean) {
        String jsonString = gson.toJson(bean, ItemAppendBean.class);
        return jsonString;
    }

    public List<ItemAppendBean> deserializeList(String jsonString) {
        List<ItemAppendBean> ps = gson.fromJson(jsonString, new TypeToken<List<ItemAppendBean>>() {
        }.getType());
        return ps;
    }

    public String serialize(List<ItemAppendBean> beans) {
        String jsonString = gson.toJson(beans);
        return jsonString;
    }


    public ItemAppendBean deserialize(String jsonString) {
        ItemAppendBean userEntity = gson.fromJson(jsonString, ItemAppendBean.class);
        return userEntity;
    }


    public IDinfoBean deserializeIDinfo(String jsonString) {
        IDinfoBean iDinfoBean = gson.fromJson(jsonString, IDinfoBean.class);
        return iDinfoBean;
    }
    public String serializeIDinfo(IDinfoBean bean) {
        String jsonString = gson.toJson(bean, IDinfoBean.class);
        return jsonString;
    }

}
