const app = getApp()
Page({

  data: {
    userInfo: null,
  },

  logout: function(){
    wx.showModal({
      title: '提示',
      content: '确定要注销吗？',
      success: function (res) {
        if (res.confirm) {
          wx.setStorage({ key: 'userInfo', data: null })
          wx.navigateTo({url: '/pages/login/login'}) 
        } else if (res.cancel) {}
      }
    })
    
  },
  
  onLoad: function (options) {},

  onReady: function () {},

  onShow: function () {
    if(app.validateUser()){
      this.setData({
        userInfo: wx.getStorageSync('userInfo')
      })
    };
  },

  onHide: function () {},

  onUnload: function () {},

  onPullDownRefresh: function () {},
 
  onReachBottom: function () {},
   
  onShareAppMessage: function () {}
})