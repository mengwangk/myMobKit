angular.module('resources.messages', [ 'ngResource' ]);
angular.module('resources.messages').factory(
		'Messages',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var messagesService = {};									
					messagesService.getAllMessages = function() {							
							var path = $rootScope.host + '/services/api/messaging/';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : false
										}
									});
					};
					messagesService.getConversation = function(threadID) {							
							var path = $rootScope.host + '/services/api/messaging/';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											params: {
												ThreadID: threadID												
											},
											isArray : false
										}
									});
					};
					
					messagesService.sendMessage = function(to, msg) {							
							var path = $rootScope.host + '/services/api/messaging/';
							return $resource(path, {},
									{
										get : {
											method : 'POST',
											params: {
												To: to,
												Message: msg
											},
											isArray : false
										}
									});
					};
					
					messagesService.getMessageStatus = function(msgID) {							
							var path = $rootScope.host + '/services/api/messaging/status/' + msgID;
							return $resource(path, {},
									{
										get : {
											method : 'GET',											
											isArray : false
										}
									});
					};
					return messagesService;

		} ]);