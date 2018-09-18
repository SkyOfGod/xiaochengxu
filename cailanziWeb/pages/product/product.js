var app = getApp();
Page({
  data: {
    categoriesList: [],
    products: null,
    pageNo: 0,
    id:null,
    total:0,
    skuName:null,
    searchType:0,//0为默认种类搜索，1为搜索框搜索
    bHide:true,
    setProduct:null,
    storeInput:0,
    priceInput:0,
  },

  onLoad: function (options) {
    this.loadCategories();
    this.getProducts(null);
  },

  onShow: function(){
    this.setData({ skuName: null })
  },

  loadCategories: function(){
    var that = this;
    wx.request({
      url: app.globalData.urlPrefix + '/product/web/getCategories',
      success: function (res) {
        that.setData({ categoriesList: res.data });
      },
      error: function (e) {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
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
    this.setData({categoriesList: list});
    this.clickGetProducts(e);
  },

  getProducts: function (skuName) {
    wx.showLoading({title: '加载中',icon: 'loading'});
    var that = this;
    var products = this.data.products;
    if (!products) {products = [];}
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
        type: userInfo.type,
        pageNo: page,
        pageSize: 10,
        skuName: skuName,
      },
      header: {"Content-Type": "application/x-www-form-urlencoded"},
      success: function (res) {
        wx.hideLoading();
        if (res.data.status===200) {
          if (res.data.data.length > 0) {
            for (var i = 0; i < res.data.data.length; i++) {
              products.push(res.data.data[i]);
            }
            that.setData({ products: products, total: res.data.count });
          }else{
            if (that.data.total>0){
              wx.showToast({title: '我也是有底线的',icon: 'none',duration: 1000});
            }
          }
        }
      },
      error: function (e) {
        wx.showToast({title: '网络异常！',duration: 2000,icon:'none'});
      }
    })
  },

  lower: function () {
    var page = this.data.pageNo + 1;
    this.setData({ pageNo: page });
    var type = this.data.searchType;
    if (type==0){
      this.getProducts(null);
    } else if (type==1){
      this.getProducts(this.data.skuName);
    }
  },

  clickGetProducts: function (e) {
    this.setData({ 
      pageNo: 0, 
      id: e.currentTarget.dataset.categoryid,
      products : null, 
      searchType: 0
    });
    this.getProducts(null);
  },

  skuNameInput:function(e){
    this.setData({skuName: e.detail.value})
  },

  productSearch:function(){
    this.setData({
      pageNo: 0,
      id: null,
      products: null,
      searchType: 1
    });
    this.getProducts(this.data.skuName);
  },

  setStoreAndPrice:function(e){
    var product = e.currentTarget.dataset;
    this.setData({ 
      setProduct: product, 
      bHide: false, 
      storeInput: product.num,
      priceInput: product.price/100
    });
  },

  storeInput:function(e){
    this.setData({ storeInput: e.detail.value})
  },
  priceInput: function (e) {
    this.setData({ priceInput: e.detail.value })
  },

  minusStore:function(){
    var store = this.data.storeInput;
    if (store!=0){
      this.setData({ storeInput: store - 1 })
    }
  },
  plusStore: function(){
    var store = parseInt(this.data.storeInput);
    if (store != 999) {
      this.setData({ storeInput: store + 1 })
    }
  },

  hideBox: function (e) {
    this.setData({bHide: true})
  },

  saveBox: function(){
    var that = this;
    var storeInput = this.data.storeInput;
    var priceInput = this.data.priceInput;
    if (!parseInt(storeInput) || !parseInt(priceInput)){
      wx.showToast({ title: '数据格式不正确！', duration: 2000, icon: 'none' });
      return;
    }
    wx.showLoading({ title: '保存中', icon: 'loading' });
    var userInfo = wx.getStorageSync("userInfo");
    var product = this.data.setProduct;
    
    var products = this.data.products;
    wx.request({
      url: app.globalData.urlPrefix + '/product/web/updateStorePriceVendibility',
      method: 'POST',
      data: {
        phone: userInfo.username,
        stationNo: userInfo.belongStationNo,
        type: userInfo.type,
        skuId: product.skuid,
        skuPrice: priceInput*100,
        skuStore: storeInput
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        wx.hideLoading();
        if (res.data.status === 200) {
          wx.showToast({ title: '修改成功', duration: 1000 });
          that.hideBox();
          for (var i in products){
            if (products[i].skuId === product.skuid){
              products[i].price = priceInput*100;
              products[i].num = storeInput;
              break;
            }
          }
          that.setData({ products: products});
        } else if (res.data.status === 201){
          wx.showToast({ title: res.data.msg, duration: 1000, icon: 'none' });
        }
      },
      error: function (e) {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    })
  },

  setVendibility:function(e){
    wx.showLoading({ title: '保存中', icon: 'loading' });
    var that = this;
    var userInfo = wx.getStorageSync("userInfo");
    var skuid = e.currentTarget.dataset.skuid;
    var status = e.currentTarget.dataset.status;
    var vendibility = "0";
    if (status==="0"){
      vendibility = "1";
    }
    var products = this.data.products;
    wx.request({
      url: app.globalData.urlPrefix + '/product/web/updateStorePriceVendibility',
      method: 'POST',
      data: {
        phone: userInfo.username,
        stationNo: userInfo.belongStationNo,
        type: userInfo.type,
        skuId: skuid,
        vendibility: vendibility
      },
      header: { "Content-Type": "application/x-www-form-urlencoded" },
      success: function (res) {
        wx.hideLoading();
        if (res.data.status === 200) {
          wx.showToast({ title: '修改成功', duration: 1000 });
          for (var i in products) {
            if (products[i].skuId === skuid) {
              products[i].status = vendibility;
              break;
            }
          }
          that.setData({ products: products });
        } else if (res.data.status === 201) {
          wx.showToast({ title: res.data.msg, duration: 1000, icon: 'none' });
        }
      },
      error: function (e) {
        wx.showToast({ title: '网络异常！', duration: 2000, icon: 'none' });
      }
    })
  }

})
