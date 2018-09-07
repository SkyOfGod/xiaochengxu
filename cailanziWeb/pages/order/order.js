// pages/order/order.js
var app = getApp();
Page({
  data: {
    winWidth: 0,
    winHeight: 0,
    // tab切换  
    currentTab: 0,
    page: 0,
    orders: null,//待发货
    orders2: null,//待配送
    orders4: null,//已完成
    ifToDelivery:false,
    ifToFinish:false,
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
      if (current == 0 && that.data.orders==null) {
        that.loadOrderList();
      }
      if (current == 1 && that.data.orders2==null) {
        that.loadOrder2List();
      }
      if (current == 3 && that.data.orders4 == null) {
        that.loadOrder4List();
      }
    };
  },
  //待发货
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
        belongStationNo: userInfo.belongStationNo
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      success: function (res) {
        if (res.data.data&&res.data.data.length>0){
          that.setData({orders: res.data});
        }else{
          that.setData({orders: null});
        }
        wx.hideLoading();
      }, 
      fail: function () {
        wx.showToast({
          title: '网络异常！',
          duration: 2000
        });
      }
    });

  },
  //待配送
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
          that.setData({orders2: res.data});
        } else {
          that.setData({orders2: null});
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({title: '网络异常！',duration: 2000});
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
        belongStationNo: userInfo.belongStationNo
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.data && res.data.data.length > 0) {
          that.setData({ orders4: res.data });
        } else {
          that.setData({ orders4: null });
        }
        wx.hideLoading();
      },
      fail: function () {
        wx.showToast({ title: '网络异常！', duration: 2000 });
      }
    });
  },

  initSystemInfo: function () {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          winWidth: res.windowWidth,
          winHeight: res.windowHeight
        });
      } 
    });
  },

  toDelivery: function (e){
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认要转待配送吗？',
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
      url: app.globalData.urlPrefix + '/order/web/updateOrderShopStatusToDelivery',
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

  toFinish: function (e) {
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认收货吗？',
      success: function (res) {
        if (res.confirm) {
          that.saveToFinish(e);
        } else if (res.cancel) { }
      }
    })
  },

  saveToFinish: function (e) {
    var that = this;
    var orderId = e.currentTarget.dataset.orderid;
    wx.request({
      url: app.globalData.urlPrefix + '/order/web/updateOrderShopStatusToFinish',
      method: 'POST',
      data: {
        orderId: orderId,
        orderStatus: '35000'
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        if (res.data.status == 200) {
          that.loadOrder2List();
          that.loadOrder4List();
          wx.showToast({
            title: '收货成功',
            icon: 'success',
            duration: 500
          });
        }
      }
    })
  },

  onLoad: function (options) {
    if (app.validateUser()) {
      this.initSystemInfo();
      this.loadOrderList();
      this.loadOrder2List();
    };
  },

  onShow: function(){
    if (wx.getStorageSync('userInfo').type == 1) {
      this.setData({ifToDelivery: true});
    }else{
      this.setData({ifToDelivery: false});
    }
    if (wx.getStorageSync('userInfo').type == 2) {
      this.setData({ ifToFinish: true });
    } else {
      this.setData({ ifToFinish: false });
    }
  },

  onPullDownRefresh: function () {
    var current = this.data.currentTab
    if (current == 0) {
      this.loadOrderList();
    }
    if (current == 1) {
      this.loadOrder2List();
    }
    if (current == 3) {
      this.loadOrder4List();
    }
    wx.stopPullDownRefresh();//没有就不合并画面
  },


})
