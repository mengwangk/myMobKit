angular.module('resources.media', [ 'ngResource' ]);
angular.module('resources.media').factory(
		'Media',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var mediaServices = {};									
					
					mediaServices.getAllMedia = function(media) {							
							var path = $rootScope.host + '/services/api/media/' + media;
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : false
										}
									});
					};
					
					mediaServices.deleteMedia = function(media, id) {							
							var path = $rootScope.host + '/services/api/media/' + media + '/' + id;
							return $resource(path, {},
									{
										get : {
											method : 'DELETE',
											isArray : false
										}
									});
					};
					
					return mediaServices;

		} ]);