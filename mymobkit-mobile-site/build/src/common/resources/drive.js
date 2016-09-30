angular.module('resources.drive', [ 'ngResource' ]);
angular.module('resources.drive').factory(
		'Drive',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var driveService = {};	
					
					var s4 = function(){
						return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
					};
					
					var guid = function() {
						return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
						s4() + '-' + s4() + s4() + s4();
					};
					
					
					driveService.getImages = function(folderName, count) {							
						var path = $rootScope.host + '/services/api/drive';
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										params: {
											guid: guid(),	
											folder: folderName,		
											mime: 'image/jpeg',
											pageSize: count
										},
										isArray : false
									}
								});
					};
					
					driveService.getDeviceInfoFile = function(folderName) {							
						var path = $rootScope.host + '/services/api/drive';
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										params: {
											guid: guid(),
											folder: folderName,		
											mime: 'text/plain'
										},
										isArray : false
									}
								});
					};
					
					driveService.getDeviceInfoFileContent = function(resourceId) {							
						var path = $rootScope.host + '/services/stream/drive';
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										params: {
											guid: guid(),
											uri: resourceId,		
											mime: 'text/plain',
											id: '1'
										},
										isArray : false
									}
								});
					};
					
					return driveService;

		}]);