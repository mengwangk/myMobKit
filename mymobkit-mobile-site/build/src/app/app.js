angular.module( 'mymobkit', [
  'templates-app',
  'templates-common',
  'ui.bootstrap', 
  'ui.router',
  'ngResource',   
  'ngGrid',
  'ngTouch',
  'resources.parameter',
  'toaster',
  'ngAnimate',
  'mymobkit.home',
  'mymobkit.about',
  'mymobkit.contact',
  'mymobkit.surveillance',
  'mymobkit.services',
  'mymobkit.media',
  'mymobkit.messaging',
  'mymobkit.mms',
  'mymobkit.video',
  'mymobkit.image',
  'mymobkit.tracker',
  'mymobkit.device',
  'mymobkit.notif',
  'mymobkit.controlpanel',
  'mymobkit.ussd',
  'mymobkit.viewer',
  'mymobkit.camera',
  'mymobkit.vcard',
  'mymobkit.call',
  'mymobkit.location',
  'mymobkit.sensor',
  'mymobkit.vcalendar',
  'mymobkit.fullscreen'
])
.config( ["$stateProvider", "$urlRouterProvider", function myAppConfig ( $stateProvider, $urlRouterProvider ) {
  $urlRouterProvider.otherwise( '/home' );
}])
.run( function run () {
})
.controller( 'AppCtrl', ["$rootScope", "$scope", "$location", "$resource", "Parameter", "$log", "$state", function AppCtrl ( $rootScope, $scope, $location, $resource, Parameter, $log, $state) {
	$rootScope.debug_mode = false;
	if (!$rootScope.debug_mode) {
		$rootScope.host = $location.protocol() + '://' + $location.host() + ':' + $location.port();
		$rootScope.app_server = 'http://www.mymobkit.com';
	} else {
		$rootScope.host = 'http://192.168.0.102:1688';
		$rootScope.surveillance_mock_url = 'http://192.168.0.102:6888';
		$rootScope.app_server = 'http://mymobkit-rtc.appspot.com';
	}
			
	var getDeviceName = function() {
    //$log.info('Get device name');
		Parameter.getParameterValue('preferences_device_unique_name').get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.deviceName = resp.parameters['preferences_device_unique_name'];
					}
				}, function err(httpResponse) {
			});
	};
	
	getDeviceName();
		
	$scope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
	if ( angular.isDefined( toState.data.pageTitle ) ) {
		$scope.pageTitle = toState.data.pageTitle + ' | myMobKit' ;
	}
	});
  
  $rootScope.fullscreen = false; 
	
}]);

