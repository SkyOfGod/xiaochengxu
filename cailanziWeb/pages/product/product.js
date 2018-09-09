var app = getApp();
Page({
  data: {
    categoriesList: [],
    products: null,
    pageNo: 0,
    id:null,
    total:0,
  },

  onShow: function (options) {
    var that = this;
    wx.request({
      url: app.globalData.urlPrefix + '/product/web/getCategories',
      success: function (res) {
        that.setData({
          categoriesList: res.data,
        });
      },
      error: function (e) {
        wx.showToast({
          title: '网络异常！',
          duration: 2000,
        });
      }
    });
  },

  kindToggle: function (e) {
    var id = e.currentTarget.dataset.categoryid, list = this.data.categoriesList;
    for (var i = 0, len = list.length; i < len; ++i) {
      if (list[i].id == id) {
        list[i].open = !list[i].open
      } else {
        list[i].open = false
      }
    }
    this.setData({
      categoriesList: list
    });
    this.clickGetProducts(e);
  },

  getProducts: function () {
    wx.showLoading({
      title: '加载中',
      icon: 'loading',
    });
    var that = this;
    var products = this.data.products;
    if (!products) {
      products = [];
    }
    var page = this.data.pageNo;
    var categoryid = this.data.id;
    var userInfo = wx.getStorageSync("userInfo");
    wx.request({
      url: app.globalData.urlPrefix + '/product/web/getProductsByCategoryId',
      method: 'POST',
      data: {
        categoryId: categoryid,
        phone: userInfo.username,
        stationNo: userInfo.belongStationNo,
        pageNo: page
      },
      header: {"Content-Type": "application/x-www-form-urlencoded"},
      success: function (res) {
        if (res.data) {
          if (res.data.length > 0) {
            for (var i = 0; i < res.data.length; i++) {
              products.push(res.data[i]);
            }
            that.setData({ products: products, total: products.length });
          }else{
            if (that.data.total>0){
              wx.showToast({
                title: '我也是有底线的',
                icon: 'none',
                duration: 300
              });
            }
          }
        }
        wx.hideLoading();
      },
      error: function (e) {
        wx.showToast({
          title: '网络异常！',
          duration: 2000,
        });
      }
    })
  },

  lower: function () {
    var page = this.data.pageNo + 1;
    this.setData({ pageNo: page });
    this.getProducts();
  },

  clickGetProducts: function (e) {
    this.setData({ 
      pageNo: 0, 
      id: e.currentTarget.dataset.categoryid,
      products : null, 
      total: 0,
      });
    this.getProducts(e);
  }


})
