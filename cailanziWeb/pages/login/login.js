const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    code: null,
  },

  // 获取输入账号
  phoneInput: function (e) {
    this.setData({
      username: e.detail.value
    })
  },

  // 获取输入密码
  passwordInput: function (e) {
    this.setData({
      password: e.detail.value
    })
  },

  onShow:function(){
    var that = this;
    wx.login({
      success: function (res) {
        that.setData({ code: res.code })
      }
    })
  },

  // 登录
  login: function () {
    wx.showLoading({ title: '加载中', icon: 'loading' });
    var that = this;
    wx.request({
      url: app.globalData.urlPrefix + '/user/web/login?username=' + that.data.username
        + '&password=' + that.data.password + '&code=' + that.data.code,
      success: function (res) {
        wx.hideLoading();
        var status = res.data.status;
        if (status == 200) {
          wx.setStorageSync('userInfo', res.data.data);
          wx.showToast({
            title: '登录成功', 
            icon: 'success',
            duration: 2000
          })
          wx.navigateBack({})
        } else { 
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          }) 
        }
      },
      fail: function () {
        wx.hideLoading();
        wx.showToast({title: '网络异常！',icon: 'none',duration: 2000})
      } 
    });
  }

})