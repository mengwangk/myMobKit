angular.module('resources.gcm', [ 'ngResource' ]);
angular.module('resources.gcm').factory(
		'Gcm',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					
					var s4 = function(){
						return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
					};
					
					var guid = function() {
						return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
						s4() + '-' + s4() + s4() + s4();
					};
					
					var gcmService = {};									
					gcmService.getAllNotifications = function() {							
							var path = $rootScope.host + '/services/api/gcm/';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											params: {
												guid: guid()		
											},
											isArray : false
										}
									});
					};
					
					gcmService.purge = function() {							
						var path = $rootScope.host + '/services/api/gcm/';
						return $resource(path, {},
								{
									get : {
										method : 'DELETE',
										params: {
											guid: guid()		
										},
										isArray : false
									}
								});
					};
					
					gcmService.getDevices = function(email, deviceId) {							
						var path = $rootScope.app_server + '/service/device/group/' + email + '/' + deviceId;
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										params: {
											guid: guid()		
										},
										isArray : false
									}
								});
					};
					
					gcmService.postAction = function(deviceId, actionType, actionCommand, actionCommandValue) {							
						var path =  $rootScope.host + '/services/api/gcm/';
						return $resource(path, {},
								{
									get : {
										method : 'POST',
										params: {
											DeviceId: deviceId,
											ActionType: actionType,
											ActionCommand: actionCommand,
											ActionCommandValue: actionCommandValue
										},
										isArray : false
									}
								});
					};					
					return gcmService;
		}]);