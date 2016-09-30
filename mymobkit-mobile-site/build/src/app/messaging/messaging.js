angular.module( 'mymobkit.messaging', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'resources.parameter',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'messaging', {
    url: '/messaging',
    views: {
      "main": {
        controller: 'MessagingCtrl',
        templateUrl: 'messaging/messaging.tpl.html'
      }
    },
    data:{ pageTitle: 'Messaging' }
  });
}])

.controller( 'MessagingCtrl', ["$rootScope", "$scope", "Messages", "Parameter", "$window", "$timeout", "$http", "$location", "$anchorScroll", function MessagingCtrl($rootScope, $scope, Messages, Parameter, $window, $timeout, $http, $location, $anchorScroll) {

		var isNotDefined = function(val) { 
			return (angular.isUndefined(val) || val === null);
		};	
		
		var configureSmsApi = function(){
			$scope.allMessagesLink = $rootScope.host + '/services/api/messaging/';
			$scope.parameterLink = $rootScope.host + '/services/api/parameter/';
			
			$scope.messagingForm =  {
					preferences_messaging_aging_method: 'Days',
					preferences_messaging_aging_days: 7,
					preferences_messaging_aging_size: 500,
					preferences_saved_sent_messages: "true"
			};
			getParameterValue('preferences_messaging_aging_method', false);
			getParameterValue('preferences_messaging_aging_days', true);
			getParameterValue('preferences_messaging_aging_size', true);
			getParameterValue('preferences_saved_sent_messages', false);
		};
		
		var getParameterValue = function(key, isNumber) {
			Parameter.getParameterValue(key).get().$promise.then(
					function success(resp, headers) {
						if (resp.isSuccessful) {
							if (!isNumber) {
								$scope.messagingForm[key] = resp.parameters[key];
							} else {
								var value = parseInt(resp.parameters[key], 10);
								if (value > 0) {
									$scope.messagingForm[key] = value;
								}
							}
						}
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
				});
		};
		
		
		var getAllMessages = function(){
			// Use get to return an object, query for array
			Messages.getAllMessages().get().$promise.then(
					function success(resp, headers) {						
						$scope.allMessages = resp;
						$scope.messages = $scope.allMessages.messages;	
						if ($scope.messages.length > 0) {
							getConversation($scope.messages[0].threadID);
						}
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
					});
		};	
		var getConversation = function(threadID) {
			// Use get to return an object, query for array
			Messages.getConversation(threadID).get().$promise.then(
					function success(resp, headers) {	
						$scope.currentThreadID = threadID;
						$scope.currentConversation = resp;		
						$scope.currentPhoneNo = $scope.currentConversation.messages[0].number;
						angular.forEach($scope.currentConversation.messages, function (message) {
							message.id = parseFloat(message.id);
						});
						
						$scope.scrolltoEnd();
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
					});
		};
			
		$scope.getListAlignment = function(msg) {
			if (msg.sender.toUpperCase() == 'ME') { 
				return "left clearfix";
			} else {
				return "right clearfix";
			}
		};
		
		var sendMessage = function(to, msg) {
			Messages.sendMessage(to, msg).get().$promise.then(
					function success(resp, headers) {
						// clear the input
						$scope.btnMessage = '';
						
						if (resp.isSuccessful) {
							$scope.statusAvailable = false;
							$scope.messageStatus = '';
							$scope.errorDescription = '';
							checkMessageStatus(resp.message.id);
						}
						
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
				});
		};
		
		$scope.gotoBottom = function(){
			// set the location.hash to the id of
			// the element you wish to scroll to.
			$location.hash('bottom');

			// call $anchorScroll()
			$anchorScroll();
			
			//$window.alert('5 seconds');
			
		};
		
		$scope.scrolltoEnd = function() {
			var timer = $timeout(
					function() {	
						$scope.gotoBottom();
					},
			500);			
		};
		
		var checkMessageStatus = function(msgID) {
			if ($scope.statusAvailable) {
				if ($scope.messageStatus === 'Sent' || $scope.messageStatus === 'Delivered') {
					getConversation($scope.currentThreadID);
				} else {
					$window.alert('Failed to send message. Error code: [' + $scope.errorDescription + ']');
				}
				return;
			}
			var msgTimer = $timeout(
					function() {			
						Messages.getMessageStatus(msgID).get().$promise.then(
							function success(resp, headers) {
								if (resp.status !== '' && resp.status !== 'Queued') {
									$scope.statusAvailable = true;
									$scope.messageStatus = resp.status;
									$scope.errorDescription = resp.description;
									checkMessageStatus(msgID);
								} else {
									checkMessageStatus(msgID);
								}
							}, function err(httpResponse) {
								$scope.statusAvailable = true;
								$scope.errorMsg = httpResponse.status;
							});
					}, 
			300);			
		};
		
		$scope.onSendMessage = function(){
			if (!isNotDefined($scope.currentConversation) && $scope.btnMessage !== ''){
				// Send the message
				var to = $scope.currentPhoneNo;
				var msg = $scope.btnMessage;
				sendMessage(to, msg);
			}
		};
		
		$scope.onRefreshConveration = function(){
			if (!isNotDefined($scope.currentConversation)){
				// Refresh the current conversation
				getConversation($scope.currentThreadID);				
			}
		};
		
		$scope.submitMessagingForm =  function(item, event) {
			$scope.messageFormStatus = true;
			var params = angular.copy($scope.messagingForm);
			$http(
					{
					method: 'POST', 
					url: $scope.parameterLink, 
					params:  params
					}
				).
				success(function(data, status, headers, config) {
					$scope.messageFormStatus = false;
					$scope.saveStatus = "Successfully saved the parameters";
				}).
				error(function(data, status, headers, config) {
					$scope.messageFormStatus = false;
					$scope.saveStatus = "Failed to save parameters";
				});
		};
		
		$scope.messages = [];	
		$scope.selectedMessage = [];	
		$scope.messagingForm =  {
				preferences_messaging_aging_method: 'Days',
				preferences_messaging_aging_days: 7,
				preferences_messaging_aging_size: 500,
				preferences_saved_sent_messages: "true"
		};
		$scope.messageFormStatus = false;
		
		$scope.messageGrid = { 
			data: 'messages',
			enableRowSelection: true,
            enableCellEditOnFocus: false,
			showGroupPanel: true,
            multiSelect: false, 
			showColumnMenu:true,
			showFilter:false,
			enableColumnResize:true,
			columnDefs: [
			{field: 'id', displayName: 'ID'},
			{field: 'threadID', displayName: 'Thread ID', visible:false},
			{field: 'messageType', displayName: 'Type', visible:false},
			{field: 'number', displayName: 'Phone No', visible:false},
			{field: 'serviceCenter', displayName: 'Service Center No', visible:false},
			{field: 'date', displayName: 'Date'},			
			{field: 'sender', displayName: 'From'},
			{field: 'receiver', displayName: 'To'},
			{field: 'message', displayName: 'Message'},
			{field: 'read', displayName: 'Read', visible:false}					
			],
			selectedItems : $scope.selectedMessage,
			afterSelectionChange: function(rowItem) {
				if (rowItem.selected) {
					// Get all messages related to the thread
					var threadID = $scope.selectedMessage[0].threadID;
					getConversation(threadID);
				}
			}
		};
			
		configureSmsApi();
		getAllMessages(); 
}]
)
;
