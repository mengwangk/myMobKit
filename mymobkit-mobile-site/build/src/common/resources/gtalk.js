angular.module('resources.gtalk', [ 'ngResource' ]);
angular.module('resources.gtalk').factory(
		'GTalk',
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
					
					var gTalkService = {};									
					
					gTalkService.postAction = function(deviceId, command, args) {							
						var path =  $rootScope.host + '/services/api/gtalk/';
						return $resource(path, {},
								{
									get : {
										method : 'POST',
										params: {
											TargetDeviceId: deviceId,
											Command: command,
											Args: args
										},
										isArray : false
									}
								});
					};					
					return gTalkService;
		}]);