package com.yingli.chargingpile.EventBusMsg;

import com.yingli.chargingpile.bean.ChargingBean;

import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class RefreshRateMsg {
    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;
    public RefreshRateMsg(List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList) {
        this.priceConfBeanList = priceConfBeanList;
    }

    public List<ChargingBean.DataBean.PriceConfBean> getPriceConfBeanList() {
        return priceConfBeanList;
    }

    public void setPriceConfBeanList(List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList) {
        this.priceConfBeanList = priceConfBeanList;
    }
}
