//app.js
App({
  globalData: {
    urlPrefix: 'http://localhost:8090',
    // urlPrefix: 'https://api.1000heng.xyz',
    // userInfo: null,
  },
  validateUser: function () {
    var that = this;
    var temp = wx.getStorageSync('userInfo');
    if (!temp) {
      wx.navigateTo({ 
        url: '/pages/login/login',
      })
      return false;
    } else {
      // wx.request({
      //   url: that.globalData.urlPrefix + '/user/web/isExitSign?sign=' + temp.sign,
      //   success: function (res) { 
      //     if (res.data.status != 200) {
      //       wx.navigateTo({
      //         url: '/pages/login/login',
      //       })
      //     }
      //   }
      // })
    }
    return true;
  }



})