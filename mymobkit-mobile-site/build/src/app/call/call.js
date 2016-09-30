angular.module( 'mymobkit.call', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'call', {
    url: '/call',
    views: {
      "main": {
        controller: 'CallCtrl',
        templateUrl: 'call/call.tpl.html'
      }
    },
    data:{ pageTitle: 'Call' }
  });
}])

.controller( 'CallCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", function CallCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll) {

	var configureCallaApi = function(){
		$scope.callLink = $rootScope.host + '/services/api/call';
	};
	
	configureCallaApi();
}]
)
;
