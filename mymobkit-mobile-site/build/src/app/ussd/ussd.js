angular.module( 'mymobkit.ussd', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'ussd', {
    url: '/ussd',
    views: {
      "main": {
        controller: 'UssdCtrl',
        templateUrl: 'ussd/ussd.tpl.html'
      }
    },
    data:{ pageTitle: 'USSD' }
  });
}])

.controller( 'UssdCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", function UssdCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll) {

	var configureUssdApi = function(){
		$scope.ussdLink = $rootScope.host + '/services/api/ussd/';
	};
	
	configureUssdApi();
}]
)
;
