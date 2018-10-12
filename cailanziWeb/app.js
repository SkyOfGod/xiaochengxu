//app.js
App({
  globalData: {
    //urlPrefix: 'http://localhost:8090',
    //urlPrefix: 'http://119.23.232.221:8080',
    //imgUrlPrefix: 'http://119.23.232.221:8080',

    urlPrefix: 'https://api.1000heng.xyz',
    imgUrlPrefix: 'https://api.1000heng.xyz',
    code:null,
  },
  validateUser: function () {
    var that = this;
    var userInfo = wx.getStorageSync('userInfo');
    if (!userInfo) {
      wx.navigateTo({ url: '/pages/login/login' }) 
      return false;
    } else {
      wx.request({
        url: that.globalData.urlPrefix + '/user/web/isExitSign?sign=' + userInfo.sign,
        success: function (res) { 
          if (res.data.status != 200) {
            wx.navigateTo({ url: '/pages/login/login'})
          }
        }
      })
    }
    return true;
  },

  connectSocket: function(){
    var userInfo = wx.getStorageSync('userInfo');
    wx.connectSocket({
      // url: 'ws://localhost:8080/websocket?' + userInfo.username,
      url: 'wss://api.1000heng.xyz/websocket?' + userInfo.username,
      header: {'content-type': 'application/json'},
      method: "GET"
    })
    wx.onSocketOpen(function (res) {
      console.log('WebSocket open！')
    }) 
    wx.onSocketError(function (res) {
      console.log('WebSocket error！')
    })
    
  }



})