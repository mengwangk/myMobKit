angular.module( 'mymobkit.fullscreen', [
  'ui.router',
  'placeholders',
  'ui.bootstrap'
])
.config(function config( $stateProvider ) {
  $stateProvider.state( 'fullscreen', {
    url: '/fullscreen',
    views: {
      "main": {
        controller: 'FullScreenCtrl',
        templateUrl: 'fullscreen/fullscreen.tpl.html'
      }
    },
    data:{ pageTitle: 'Surveillance - Full Screen Mode' }
  });
})
.controller( 'FullScreenCtrl', function FullScreenCtrl($rootScope, $scope, $window, $timeout, $http, $location, $resource, $anchorScroll, $log) {


  var s4 = function(){
      return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    };
    
  var guid = function() {
      return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
      s4() + '-' + s4() + s4() + s4();
  };
    
  var configureFullScreen = function() {
        var surveillance = $resource($rootScope.host + "/services/surveillance/url?id=" + guid());
        var obj  = surveillance.get(function() {
          $scope.surveillanceUrl = obj.url;       
          $scope.mjpegUrl = $scope.surveillanceUrl  + '/video/live.mjpg?id=' + guid();   
          $rootScope.fullscreen = true;      
      });
  };
  configureFullScreen();
}
);
