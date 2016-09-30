angular.module('services.device', [ 'ngResource' ]);
angular.module('services.device').factory(
		'Device',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var devices = [];		
					var deviceService = {};	
					
					deviceService.addDeviceInfo = function(obj) {							
						devices.push(obj);
					};
					
					deviceService.getDeviceInfo = function(name){
						for (var i = 0, len = devices.length; i < len; i++) {
							var device = devices[i];
							if (device.deviceName.toUpperCase() == name.toUpperCase()) {
								return device;
							}
							return null;
						}
					};
					
					return deviceService;

		}]);