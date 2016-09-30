angular.module( 'mymobkit.camera', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'camera', {
    url: '/camera',
    views: {
      "main": {
        controller: 'CameraCtrl',
        templateUrl: 'camera/camera.tpl.html'
      }
    },
    data:{ pageTitle: 'Camera' }
  });
}])

.controller( 'CameraCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", function CameraCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll) {

	var configureCameraApi = function(){
		$scope.cameraLink = $rootScope.host + '/services/stream/camera';
	};
	
	configureCameraApi();
}]
)
;
