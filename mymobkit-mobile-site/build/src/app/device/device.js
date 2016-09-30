angular.module( 'mymobkit.device', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.gcm',
  'resources.parameter',
  'resources.drive',
  'services.device'
])

.config(["$stateProvider", "$sceProvider", function config( $stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'device', {
    url: '/device',
    views: {
      "main": {
        controller: 'DeviceCtrl',
        templateUrl: 'device/device.tpl.html'
      }
    },
    data:{ pageTitle: 'My Devices' }
  });
}])
.controller( 'DeviceCtrl', ["$rootScope", "$scope", "Gcm", "Parameter", "Drive", "Device", "$window", "$timeout", "$uibModal", "$http", "$log", function DeviceCtrl($rootScope, $scope, Gcm, Parameter, Drive, Device, $window, $timeout, $uibModal, $http, $log) {

	$scope.deviceInfo =  {
		system_device_id: '',
		preferences_device_unique_name: '',
		preferences_device_email_address: '',
		preferences_device_tracking: false,
		preferences_alarm_image_drive_storage: false	
	};
	
	$scope.slideInterval = 0;
	$scope.slideActive = 0;
	$scope.noWrapSlides = false;
	$scope.loadingInProgress = true;
	
	$scope.msg =  {
			title: 'Alert',
			content: 'Unable to retrieve devices.'
	};
	$scope.devices = [];
	
	var isNotDefined = function(val) { 
		return (angular.isUndefined(val) || val === null);
	};	
	
	var s4 = function(){
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	};
	
	var guid = function() {
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		s4() + '-' + s4() + s4() + s4();
	};
	
	var loading = function() {
		$scope.modalInstance = $uibModal.open({
			templateUrl: 'assets/view/loading.html',
			controller: 'LoadingModalCtrl',
			backdrop : 'static',
			keyboard: false,
			size: 'sm'
		});
	
		$scope.modalInstance.result.then(function () {
			// Do something			
		}, function () {
			$log.info('Dismissed at: ' + new Date());
		});
	};
	
	var loadDeviceInfo = function() {
		var key_drive_storage = 'preferences_alarm_image_drive_storage';
		var key_device_tracking = 'preferences_device_tracking';
		var key_device_name ='preferences_device_unique_name';
		getParameterValue(key_drive_storage);
		getParameterValue(key_device_tracking);
		getParameterValue(key_device_name);
	};
	
	var getParameterValue = function(key) {
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.deviceInfo[key] = resp.parameters[key];
					}
				}, function err(httpResponse) {
			});
	};
	
	var loadDevices = function() {
		var key = 'preferences_device_email_address';
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.deviceInfo[key] = resp.parameters[key];
						getDeviceId();
					} else {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						showMsg($scope.msg);
					}
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showMsg($scope.msg);
			});
	};
	
	var getDeviceId = function() {
		var key = 'system_device_id';
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.deviceInfo[key] = resp.parameters[key];
						getRegisteredDevices();						
					} else {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						showMsg($scope.msg);
					}
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showMsg($scope.msg);
			});
	};
	
	var getRegisteredDevices = function() {
		var email = $scope.deviceInfo['preferences_device_email_address'];
		var deviceId = $scope.deviceInfo['system_device_id'];
		
		Gcm.getDevices(email, deviceId).get().$promise.then(
				function success(resp, headers) {	
					if (resp.responseCode === 0) {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						// Show message if device tracking is not enabled
						if ($scope.deviceInfo['preferences_device_tracking'] == 'false') {
							$scope.msg.content = 'Device tracking is not enabled. Please enable the option under Services in myMobKit';
							showMsg($scope.msg);
						}
						
						// for each device, get the drive image files, and device info file
						angular.forEach(resp.devices, function(device) {
							device.images = [];
							device.slides = [];
							device.isServiceStarted = false;
							device.uri = '';
							device.deviceInfo = [];		
							
							getDriveImages(device);
							getDriveDeviceInfo(device);
						});
						
						// Show the images
						$scope.devices = resp.devices;
						
					} else {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						showMsg($scope.msg);
					}					
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showMsg($scope.msg);
				}
		);		
	};
	
	var showMsg = function (obj) {
		var modalInstance = $uibModal.open({
			templateUrl: 'assets/view/msg.html',
			controller: 'MsgModalCtrl',
			resolve: {
				msg: function () {
					return obj;
				}
			}
		});

		modalInstance.result.then(function (obj) {
			// Do nothing
		}, function () {
			$log.info('Dismissed at: ' + new Date());
		});
	};
	
	
	var getDriveImages= function(device) {
		Drive.getImages(device.deviceId, 10).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						device.images = resp.fileInfos;			
						if (device.images.length > 0) {
							var slides = [];
							device.slides = slides;
							for (var i = 0, len = device.images.length; i < len; i++) {
								var image = device.images[i];
								slides.push({
									image: $rootScope.host + '/services/stream/drive?id=1&mime=image/jpeg&uri=' + encodeURIComponent(image.driveId) + '&guid=' + guid(),
									text: image.title,
									id: i
								});
								if (i == 10) { 
									break; 
								}
							}
						} else {
							device.slides.push({
								image: 'assets/images/placeholder.png',
								text: 'No image captured',
								id:0
							});
						}
					}
				}, function err(httpResponse) {
					
				}
		);
	};
	
	var getDriveDeviceInfo = function(device) {
		Drive.getDeviceInfoFile(device.deviceId).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						device.deviceInfo = resp.fileInfos;
						var found = false;
						if (device.deviceInfo.length > 0) {
							for (var i =0; i < device.deviceInfo.length; i++) {
								var deviceInfo = device.deviceInfo[i];
								if (deviceInfo.title.startsWith(device.deviceId)){
									getDriveDeviceInfoContent(device, deviceInfo.driveId);
									found = true;
								}
							}							
						}													
					}
				}, function err(httpResponse) {
					
				}
		);
	};
	
	var getDriveDeviceInfoContent = function(device, resourceId) {
		Drive.getDeviceInfoFileContent(resourceId).get().$promise.then(
				function success(resp, headers) {
					device.isServiceStarted = resp.isServiceStarted;
					if (device.isServiceStarted || device.isServiceStarted == 'true') {
						device.uri = resp.uri;
					}
				}, function err(httpResponse) {
					
				}
		);
	};

	//loading();
	loadDeviceInfo();
	loadDevices();	
}])
.controller( 'LoadingModalCtrl', ["$rootScope", "$scope", "$uibModalInstance", function LoadingModalCtrl($rootScope, $scope, $uibModalInstance) {
	
}]).controller( 'MsgModalCtrl', ["$rootScope", "$scope", "$uibModalInstance", "msg", function MsgModalCtrl($rootScope, $scope, $uibModalInstance, msg) {
	$scope.msg = msg;
	$scope.close = function () {
		$uibModalInstance.close(msg);
	};	
}])
;


		