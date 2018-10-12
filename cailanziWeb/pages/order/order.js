// pages/order/order.js
var app = getApp();
Page({
  data: {
    imgUrlPrefix: app.globalData.imgUrlPrefix,
    // tab切换  
    currentTab: 0,
    page: 0,
    orders: null,//待备货
    orders2: null,//待收货
    orders3: null,//待配送
    orders4: null,//已完成
    orders5: null,//已退款
    isReadyer:false,//是否是备货员
    isSender:false,//是否是收货员
    nowDate:null,//当前日期
    readyDate: null,
    readyEndDate: null,
    finishDate: null,
    finishTotal:0,
    returnDate:null,
  },
  bindChange: function (e) {
    var that = this;
    that.setData({ currentTab: e.detail.current });
  },
  swichNav: function (e) {
    var that = this;
    if (that.data.currentTab === e.target.dataset.current) {
      return false;
    } else {
      var current = e.target.dataset.current;
      that.setData({
        currentTab: parseInt(current)
      });
      if (current == 0) {
        that.loadOrderList();
      }
      if (current == 1) {
        that.loadOrderList();
        that.loadOrder2List();
      }
      if (current == 2) {
        that.loadOrder2List();
        that.loadOrder3List();
      }
      if (current == 3) {
        that.loadOrder3List();
        // this.setData({ finishDate: this.data.nowDate });
        that.loadOrder4List();
      }
      if (current == 4) {
        // this.setData({ returnDate: this.data.nowDate });
        that.loadOrder5List();
      }
    };
  },
  //待备货
  loadOrderList: function () {
    wx.showLoading({title: '加载中',icon: 'loading'});
    var that = this; 
    var userInfo = wx.getStorageSync('userInfo');
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/orderList',
      method: 'POST',
      data: {
        type: userInfo.type,
        username: userInfo.username,
        belongStationNo: userInfo.belongStationNo,
        startTime: that.data.readyDate,
        endTime: that.data.readyEndDate
      },
      header: {"Content-Type": "application/x-www-form-urlencoded"},
      success: function (res) {
        if (res.data.data&&res.data.data.length>0){
          that.setData({ orders: res.data.data});
        }else{
          that.setData({orders: null});
        }
        wx.hideLoading();
      }, 
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    });

  },
  //待收货
  loadOrder2List: function () {
    wx.showLoading({title: '加载中',icon: 'loading'});
    var that = this;
    var userInfo = wx.getStorageSync('userInfo');
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/order2List',
      method: 'POST',
      data: {
        type: userInfo.type,
        username: userInfo.username,
        belongStationNo: userInfo.belongStationNo
      },
      header: {"Content-Type": "application/x-www-form-urlencoded"},
      success: function (res) {
        if (res.data.data && res.data.data.length > 0) {
          that.setData({ orders2: res.data.data});
        } else {
          that.setData({orders2: null});
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none'});
      }
    });
  },

  //待配送
  loadOrder3List: function () {
    wx.showLoading({ title: '加载中', icon: 'loading' });
    var that = this;
    var userInfo = wx.getStorageSync('userInfo');
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/order3List',
      method: 'POST',
      data: {
        type: userInfo.type,
        username: userInfo.username,
        belongStationNo: userInfo.belongStationNo
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.data && res.data.data.length > 0) {
          that.setData({ orders3: res.data.data });
        } else {
          that.setData({ orders3: null });
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    });
  },

  //已完成
  loadOrder4List: function () {
    wx.showLoading({ title: '加载中', icon: 'loading' });
    var that = this;
    var userInfo = wx.getStorageSync('userInfo');
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/order4List',
      method: 'POST',
      data: {
        type: userInfo.type,
        username: userInfo.username,
        belongStationNo: userInfo.belongStationNo,
        startTime: that.data.finishDate, 
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.data) {
          if (res.data.data.length > 0){
            that.setData({ orders4: res.data.data});
          }else{
            that.setData({ orders4: null });
          }
          that.setData({ finishTotal: res.data.count });
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    });
  },

  //退单
  loadOrder5List: function () {
    wx.showLoading({ title: '加载中', icon: 'loading' });
    var that = this;
    var userInfo = wx.getStorageSync('userInfo');
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/order5List',
      method: 'POST',
      data: {
        type: userInfo.type,
        username: userInfo.username,
        belongStationNo: userInfo.belongStationNo,
        startTime: that.data.returnDate,
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.data) {
          if (res.data.data.length > 0) {
            that.setData({ orders5: res.data.data });
          } else {
            that.setData({ orders5: null });
          }
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    });
  },

  toDelivery: function (e){
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认转待配送吗？',
      success: function (res) {
        if (res.confirm) {
          that.saveToDelivery(e);
        } else if (res.cancel) {}
      }
    })
    
  },
 
  saveToDelivery: function(e){
    var that = this;
    var orderId = e.currentTarget.dataset.orderid;
    var username = wx.getStorageSync('userInfo').username;
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/updateOrderStatusToDelivery',
      method: 'POST',
      data: {
        orderId: orderId,
        username: username,
        orderStatus: '33000'
      },
      header: {"Content-Type": "application/x-www-form-urlencoded"},
      success: function (res) {
        if (res.data.status == 200) {
          that.loadOrderList();
          that.loadOrder2List();
          wx.showToast({
            title: '转待配送成功',
            icon: 'success',
            duration: 500
          });
        }
      }
    })
  },

  stockout: function (e) {
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认缺货吗？',
      success: function (res) {
        if (res.confirm) {
          that.saveToStockout(e);
        } else if (res.cancel) {}
      }
    })
  },

  saveToStockout: function (e){
    var that = this;
    var orderId = e.currentTarget.dataset.orderid;
    var skuId = e.currentTarget.dataset.skuid;
    var username = wx.getStorageSync('userInfo').username;
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/updateProductToStockout',
      method: 'POST',
      data: {
        orderId: orderId,
        skuId: skuId,
        username: username
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.status == 200) {
          that.loadOrderList();
          that.loadOrder2List();
          wx.showToast({
            title: '设置成功',
            icon: 'success',
            duration: 500
          });
        }
      }
    })
  },

  onLoad: function (options) {
    this.initDate();
    if (app.validateUser()) {
      this.loadOrderList();
      this.loadOrder2List();
      app.connectSocket();
      this.onMessage();
    };
  },

  onMessage: function(){
    var that = this;
    wx.onSocketMessage(function (res) {
      console.log("收到服务器消息：" + res.data)
      that.loadOrderList();
    })
  },

  onShow: function () {
    var userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      if (userInfo.type == 1) {
        this.setData({ isReadyer: true });
      } else {
        this.setData({ isReadyer: false });
      }
      if (userInfo.type == 2) {
        this.setData({ isSender: true });
      } else {
        this.setData({ isSender: false });
      }
    }
  },

  initDate: function(){
    var date = new Date();
    var time = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
    this.setData({ nowDate:time,readyDate:time,readyEndDate:time,finishDate:time,returnDate:time });
  },

  onReachBottom: function () {
    var current = this.data.currentTab
    if (current == 0) {
      this.loadOrderList();
    }
    if (current == 1) {
      this.loadOrder2List();
    }
    if (current == 2) {
      this.loadOrder3List();
    }
    if (current == 3) {
      this.loadOrder4List();
    }
  },

  bindReadyDateChange: function (e) {
    this.setData({ readyDate: e.detail.value });
    this.loadOrderList();
  },
  bindReadyEndDateChange: function (e) {
    this.setData({ readyEndDate: e.detail.value });
    this.loadOrderList();
  },
  bindDateChange: function (e) {
    this.setData({ finishDate: e.detail.value});
    this.loadOrder4List();
  },
  bindReturnDateChange: function (e) {
    this.setData({ returnDate: e.detail.value });
    this.loadOrder5List();
  },

  formSubmit: function (e) {
    var id = e.currentTarget.dataset.orderid, list = this.data.orders;
    this.kindToggleInit(id, list);
    this.setData({ orders: list });
    this.collectFormId(e.detail.formId);
  },

  formSubmit2: function (e) {
    var id = e.currentTarget.dataset.orderid, list = this.data.orders2;
    this.kindToggleInit(id, list);
    this.setData({ orders2: list });
    this.collectFormId(e.detail.formId);
  },

  formSubmit3: function (e) {
    var id = e.currentTarget.dataset.orderid, list = this.data.orders3;
    this.kindToggleInit(id, list);
    this.setData({ orders3: list });
    this.collectFormId(e.detail.formId);
  },

  formSubmit4: function (e) {
    var id = e.currentTarget.dataset.orderid, list = this.data.orders4;
    this.kindToggleInit(id, list);
    this.setData({ orders4: list });
    this.collectFormId(e.detail.formId); 
  },

  formSubmit5: function (e) {
    var id = e.currentTarget.dataset.orderid, list = this.data.orders5;
    this.kindToggleInit(id, list);
    this.setData({ orders5: list });
    this.collectFormId(e.detail.formId); 
  },

  kindToggleInit: function (id, list) {
    for (var i = 0, len = list.length; i < len; ++i) {
      if (list[i].orderId == id) {
        list[i].open = !list[i].open
      }
    }
  },

  //保存推送码
  collectFormId: function(formId){
    console.log(formId);
    if ("the formId is a mock one" == formId){
      return;
    }
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/collectFormId?formId=' + formId,
    })
  },

})

