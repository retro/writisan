var AUTH = function(successCb, errorCb) {
  var windowPropsStr = "width=800,height=600,scrollbars=yes";
  var title = "Google Login";
  var url = "/auth/google";
  var oauthWindow = window.open(url, title, windowPropsStr);
  
  if(!oauthWindow) {
    alert('Please disable your popup blocker');
  } else {

    var sweeper = window.setInterval(function() {
      if (oauthWindow.closed) {
        clearInterval(sweeper);
        F.authenticate().then(successCb, errorCb);
      }
    }, 100);
  }
}

var setToken = function(token) {
  localStorage.setItem('feathers-jwt', token);
}
