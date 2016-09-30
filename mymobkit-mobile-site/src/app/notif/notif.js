angular.module( 'mymobkit.notif', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.gcm',
  'ngGrid'
])

.config(function config( $stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'notif', {
    url: '/notif',
    views: {
      "main": {
        controller: 'NotifCtrl',
        templateUrl: 'notif/notif.tpl.html'
      }
    },
    data:{ pageTitle: 'Notifications' }
  });
})
.controller('NotifCtrl', function NotifCtrl($rootScope, $scope, Gcm, $window, $timeout, $uibModal, $http, $log) {

		var isNotDefined = function(val) { 
			return (angular.isUndefined(val) || val === null);
		};
		
		
		var getAllNotifications = function(){
			// Use get to return an object, query for array
			Gcm.getAllNotifications().get().$promise.then(
					function success(resp, headers) {						
						$scope.notifications = resp.messages;	
						
						// Massage the notifications 
						angular.forEach($scope.notifications, function (notif) {
							var obj = JSON.parse(notif.value);
							notif.deviceId = obj.deviceId;
							notif.deviceName = obj.deviceName;
							notif.registrationId = obj.registrationId;
							notif.email = obj.email;
							notif.registrationVersion = obj.registrationVersion;
							notif.dateTriggered = formatDate(obj.timestamp);
							notif.action = translateAction(notif.action);
							notif.timestamp = formatDate(notif.timestamp);
						});

						
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
					});
		};	
		
		function formatDate(date) {
			var d = new Date(date);
			var month = '' + (d.getMonth() + 1);
			var day = '' + d.getDate();
			var year = '' + d.getFullYear();
			var hh = '' + d.getHours();
			var mm = '' + d.getMinutes();
			var ss = ''  + d.getSeconds();
			
			if (month.length < 2) {
				month = '0' + month;
			}
			if (day.length < 2) {
				day = '0' + day;
			}
			if (hh.length < 2) {
				hh = '0' + hh.toString();
			}
			if (mm.length < 2) {
				mm = '0' + mm;
			}
			if (ss.length < 2) {
				ss = '0' + ss;
			}
			return [year, month, day].join('-') + " " + [hh, mm, ss].join(':');
		}
		
		var translateAction = function(action) {
			if (action == "0") {
				return "WAKE UP";
			} else if (action == "1") {
				return "MOTION DETECTION";
			} else if (action == "3") {
				return "SERVICE COMMAND";
			} else if (action == "4") {
				return "SURVEILLANCE COMMAND";
			} else if (action == "5") {
				return "CAMERA COMMAND";
			} else if (action == "6") {
				return "RING COMMAND";
			} else {
				return "UNKNOWN";
			}
		};
		
		$scope.showNotif = function (selected) {
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/notif.html',
				controller: 'NotifModalCtrl',
				resolve: {
					notif: function () {
						return selected;
					}
				}
			});

			modalInstance.result.then(function (selected) {
				// Do nothing
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};

		$scope.purge = function() {
			
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/confirm_purge.html',
				controller: 'ConfirmPurgeModalCtrl'
			});

			modalInstance.result.then(function () {
				angular.forEach($scope.notifications, function (notification) {
					
					Gcm.purge().get().$promise.then(
						function success(resp, headers) {	
							$scope.notifications = [];
						}, function err(httpResponse) {
							$scope.errorMsg = httpResponse.status;
						});
				});
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};
		
		$scope.notifications = [];	
		$scope.selectedNotification = [];	
		
		$scope.notificationGrid = { 
			data: 'notifications',
			enableRowSelection: true,
            enableCellEditOnFocus: false,
			showGroupPanel: true,
            multiSelect: false, 
			showColumnMenu:true,
			showFilter:false,
			enableColumnResize:true,
			columnDefs: [
			{field: 'id', displayName: 'ID', visible:false},
			{field: 'action', displayName: 'Action'},
			{field: 'deviceName', displayName: 'Device Name'},
			{field: 'timestamp', displayName: 'Received'}
			],
			selectedItems : $scope.selectedNotification,
			afterSelectionChange: function(rowItem) {
				if (rowItem.selected) {
					//$log.info($scope.selectedNotification[0].deviceName);
					$scope.showNotif($scope.selectedNotification[0]);
				}
			}
		};
		
		getAllNotifications();
}).controller( 'NotifModalCtrl', function NotifModalCtrl($rootScope, $scope, $uibModalInstance, notif) {
	$scope.notification = notif;
	$scope.close = function () {
		$uibModalInstance.close($scope.notification);
	};	
}).controller( 'ConfirmPurgeModalCtrl', function ConfirmPurgeModalCtrl($rootScope, $scope, $uibModalInstance) {
	$scope.yes = function () {
		$uibModalInstance.close();
	};
	
	$scope.no = function () {
		$uibModalInstance.dismiss();
	};
});


		