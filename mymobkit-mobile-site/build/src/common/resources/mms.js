angular.module('resources.mms', [ 'ngResource' ]);
angular.module('resources.mms').factory(
		'MMS',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var mmsService = {};									
					mmsService.getSupportedContentTypes = function() {							
							var path = $rootScope.host + '/services/api/mms/supportedcontenttypes';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : false
										}
									});
					};
					return mmsService;

		} ]);