angular.module( 'mymobkit.services', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'services', {
    url: '/services',
    views: {
      "main": {
        controller: 'ServicesCtrl',
        templateUrl: 'services/services.tpl.html'
      }
    },
    data:{ pageTitle: 'Services' }
  });
}])

.controller( 'ServicesCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", function ServicesCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll) {

		var isNotDefined = function(val) { 
			return (angular.isUndefined(val) || val === null);
		};	
		
		var configureStatusApi = function(){
			$scope.statusLink = $rootScope.host + '/services/api/status/';
		};
		
		configureStatusApi();
}]
)
;
