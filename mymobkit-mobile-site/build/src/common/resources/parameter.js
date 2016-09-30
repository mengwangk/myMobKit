angular.module('resources.parameter', [ 'ngResource' ]);
angular.module('resources.parameter').factory(
		'Parameter',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var parameterServices = {};									
					parameterServices.getParameterValue = function(paramKey) {							
							var path = $rootScope.host + '/services/api/parameter/';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											params: {
												key: paramKey												
											},
											isArray : false
										}
									});
					};
					return parameterServices;

		} ]);