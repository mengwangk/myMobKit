angular.module( 'mymobkit.controlpanel', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.drive',
  'resources.parameter',
  'resources.gcm',
  'resources.gtalk',
  'toaster',
  'ngAnimate',
  'ngStorage'
])

.config(function config( $stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'controlpanel', {
    url: '/controlpanel',
    views: {
      "main": {
        controller: 'ControlPanelCtrl',
        templateUrl: 'controlpanel/controlpanel.tpl.html'
      }
    },
    data:{ pageTitle: 'Control Panel' }
  });
})
.controller( 'ControlPanelCtrl', function ControlPanelCtrl($rootScope, $scope, Gcm, Drive, GTalk, Parameter, $window, $location, $timeout, $uibModal, $http, $log, toaster) {

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

	$scope.defaultDeviceInfo =  {
		system_device_id: '',
		preferences_device_unique_name: '',
		preferences_device_email_address: '',
		preferences_device_tracking: false,
		preferences_alarm_image_drive_storage: false	
	};
	
	$scope.deviceInfo = {};
	$scope.slides = [];
	$scope.slideInterval = 0;
	$scope.noWrapSlides = false;
	$scope.progressLevel = 20;
	$scope.commandSessionId = '';
	$scope.serviceStatus = '';
	$scope.loadingInProgress = true;
	
	var statusTimer = null;

	$scope.$on('$destroy', function(){
		if (statusTimer !== null) {
			$timeout.cancel(statusTimer);
		}
	});	
	
	var deviceStatus = 0;
	
	$scope.msg =  {
			title: 'Alert',
			content: 'Unable to retrieve device information.'
	};
	
	$scope.spyCamera =  {
			id: 'rear',
			interval: 3,
			uom: 'minutes'			
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
	
	var progressing = function() {
		/*
		$scope.modalInstance = $uibModal.open({
			templateUrl: 'assets/view/progress.html',
			controller: 'ProgressModalCtrl',
			backdrop : 'static',
			keyboard: false,
			size: 'sm',
			resolve: {
				progressLevel: function () {
					return $scope.progressLevel;
				}
			}		
		});
	
		$scope.modalInstance.result.then(function () {
			// Do something			
		}, function () {
			$log.info('Dismissed at: ' + new Date());
		});
		*/
	};
	
	var showServiceStatus = function(msg){
		$scope.serviceStatus = msg;
	};
	
	var showCommandStatus = function(msg){
		$scope.commandStatus = msg;
	};
	
	
	$scope.stateToClass = function(){
		if (deviceStatus == 2) {
			return "label label-success";
		} else if (deviceStatus == 1) {
			return "label label-info";
		} else {
			return "label label-warning";
		}
	};
	
	var showAlert = function (obj) {
		toaster.pop('warning', obj.title, obj.content);
	};
	
	var showMsg = function (obj) {
		toaster.pop('success', obj.title, obj.content);
		
		/*
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
		*/
	};
	
	$scope.showDeviceInfo = function () {
		var modalInstance = $uibModal.open({
			templateUrl: 'assets/view/device_info.html',
			controller: 'DeviceInfoModalCtrl',
			resolve: {
				deviceInfo: function () {
					return $scope.deviceInfo;
				}
			}
		});
		modalInstance.result.then(function (selected) {
			// Do nothing
		}, function () {
			$log.info('Dismissed at: ' + new Date());
		});
	};
	
	$scope.showViewer = function () {
		var modalInstance = $uibModal.open({
			templateUrl: 'assets/view/viewer.html',
			controller: 'ViewerModalCtrl',
			backdrop : 'static',
			keyboard: false,
			resolve: {
				slides: function () {
					return $scope.slides;
				}
			}
		});
		modalInstance.result.then(function (selected) {
			// Do nothing
		}, function () {
			$log.info('Dismissed at: ' + new Date());
		});
	};
	
	$scope.postAction = function (actionType, actionCommand) {
		//progressing();
		var deviceId = $scope.deviceInfo.deviceId;
		Gcm.postAction(deviceId, actionType, actionCommand, '').get().$promise.then(
				function success(resp, headers) {	
					if (resp.isSuccessful) {
						$scope.msg.title = 'Status';
						$scope.msg.content = 'Command sent to device.';
						//$scope.modalInstance.dismiss();
						showMsg($scope.msg);
					} else {
						$scope.msg.title = 'Status';
						$scope.msg.content = 'Unable to send command to device. ' + resp.description + '.';
						//$scope.modalInstance.dismiss();
						showAlert($scope.msg);
					}					
				}, function err(httpResponse) {
					$scope.msg.title = 'Status';
					$scope.msg.content = 'Failed to send command to device.';
					//$scope.modalInstance.dismiss();
					showAlert($scope.msg);
				}
		);
	};
	
	$scope.switchCamera = function (command, args) {
		if (deviceStatus == 2) {
			$scope.postGTalkAction(command, args);
		} else {
			$scope.msg.title = 'Surveillance';
			$scope.msg.content = 'Camera must be in surveillance mode to switch camera.';
			showAlert($scope.msg);
		}
	};
	
	$scope.triggerSurveillance = function (command, args) {
		if (deviceStatus === 0) {
			$scope.msg.title = 'Surveillance';
			$scope.msg.content = 'Service must be started to use surveillance feature.';
			showAlert($scope.msg);
		} else {
			$scope.postGTalkAction(command, args);
		}
	};
	
	$scope.triggerSpyCamera = function (command, args) {
		if (deviceStatus === 0) {			
			$scope.msg.title = 'Spy Camera';
			$scope.msg.content = 'Service must be started to use spy camera feature.';
			showAlert($scope.msg);
		} else {
			args += "~" + $scope.spyCamera.id + "~" + $scope.spyCamera.interval + "~" + $scope.spyCamera.uom;
			$scope.postGTalkAction(command, args);			
		}
	};
	
	$scope.selectSpySettings = function() {
		$scope.spySettings = !$scope.spySettings;
	};
	
	$scope.postGTalkAction = function (command, args) {
		//progressing();
		var deviceId = $scope.deviceInfo.deviceId;
		GTalk.postAction(deviceId, command, args).get().$promise.then(
				function success(resp, headers) {	
					if (resp.isSuccessful) {
						$scope.msg.title = 'Status';
						$scope.msg.content = 'Command sent to device.';
						//$scope.modalInstance.dismiss();
						showMsg($scope.msg);
					} else {
						$scope.msg.title = 'Status';
						$scope.msg.content = 'Unable to send command to device. ' + resp.description + '.';
						//$scope.modalInstance.dismiss();
						showAlert($scope.msg);
					}					
				}, function err(httpResponse) {
					$scope.msg.title = 'Status';
					$scope.msg.content = 'Failed to send command to device.';
					//$scope.modalInstance.dismiss();
					showAlert($scope.msg);
				}
		);
	};
	
	$scope.postGTalkAsyncAction = function (command, args) {
		var deviceId = $scope.deviceInfo.deviceId;
		GTalk.postAction(deviceId, command, args).get().$promise.then(
				function success(resp, headers) {	
					if (resp.isSuccessful) {
						$scope.commandSessionId = resp.sessionId;
					} else {
					}					
				}, function err(httpResponse) {
				}
		);
	};
	
	$scope.statusChecking = function() {
		statusTimer = $timeout(
				function() {
					if ($scope.commandSessionId !== '') {
						var query = $rootScope.host + '/services/api/gtalk/status/' + $scope.commandSessionId;
						$http({method: 'GET', url: query }).
							success(function(data, status, headers, config) {
								if (!isNotDefined(data.response)) {
									var response = JSON.parse(data.response);
									var serviceStatus = response.serviceStatus;
									var surveillanceStatus = response.surveillanceStatus;
									if (serviceStatus === '1' && surveillanceStatus === '0') {
										deviceStatus = 1;
										$scope.serviceStatus = 'Service: STARTED, Surveillance: STOPPED';
									} else if (serviceStatus === '1' && surveillanceStatus === '1') {
										deviceStatus = 2;
										$scope.serviceStatus = 'Service: STARTED, Surveillance: STARTED';									
									} else {
										deviceStatus = 0;
										$scope.serviceStatus = 'Service: STOPPED, Surveillance: STOPPED';
									}
								} else {
									deviceStatus = 0;
									$scope.serviceStatus = 'Service: STOPPED, Surveillance: STOPPED';
								}
								$scope.commandSessionId = '';
								$scope.postGTalkAsyncAction('status', 'get');
								$scope.statusChecking();
							}).
							error(function(data, status, headers, config) {
								$scope.commandSessionId = '';
								$scope.postGTalkAsyncAction('status', 'get');
								$scope.statusChecking();
							});		
					} else {
						$scope.postGTalkAsyncAction('status', 'get');
						$scope.statusChecking();
					}
				}, 
		5000);
	};
	
	var getDefaultDeviceInfo = function() {
		getDeviceUniqueName();
	};
	
	var getDeviceUniqueName = function() {
		var key ='preferences_device_unique_name';
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.defaultDeviceInfo[key]  = resp.parameters[key];
						getSystemDeviceId();
					}
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showAlert($scope.msg);
			});
	};
	
	var getSystemDeviceId = function() {
		var key ='system_device_id';
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.defaultDeviceInfo[key] = resp.parameters[key];
						getDriveDeviceInfo($scope.defaultDeviceInfo[key]);
					}
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showAlert($scope.msg);
			});
	};
	
	var getDriveDeviceInfo = function(deviceId) {
		Drive.getDeviceInfoFile(deviceId).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						if (resp.fileInfos.length > 0) {
							for (var i=0; i < resp.fileInfos.length; i++) {
								var deviceInfo = resp.fileInfos[i];
								if (deviceInfo.title.startsWith(deviceId)){
									getDriveDeviceInfoContent(deviceId, deviceInfo.driveId);
								}
							}							
						} else {
							//$scope.modalInstance.dismiss();
							$scope.loadingInProgress = false;
							showAlert($scope.msg);
						}													
					} else {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						showAlert($scope.msg);
					}
					
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showAlert($scope.msg);
				}
		);
	};
	
	var getDriveDeviceInfoContent = function(deviceId, resourceId) {
		Drive.getDeviceInfoFileContent(resourceId).get().$promise.then(
				function success(resp, headers) {
					$scope.deviceInfo = resp;
					$scope.deviceInfo.updated = formatDate(resp.timestamp);
					getDriveImages($scope.deviceInfo.deviceId);
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showAlert($scope.msg);
				}
		);
	};
	
	var getDriveImages= function(deviceId) {
		Drive.getImages(deviceId, 0).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						var slides = [];
						if (resp.fileInfos.length > 0) {
							for (var i = 0, len = resp.fileInfos.length; i < len; i++) {
								var image = resp.fileInfos[i];
								slides.push({
									url: $rootScope.host + '/services/stream/drive?id=1&mime=image/jpeg&uri=' + encodeURIComponent(image.driveId) + '&guid=' + guid(),
									title: image.title,
									date: image.createDate
								});								
							}
						} else {
							slides.push({
								url: 'assets/images/placeholder.png',
								title: '',
								date: ''
							});
						}
						$scope.slides = slides;
						$scope.loadingInProgress = false;
						//$scope.modalInstance.dismiss();
					} else {
						//$scope.modalInstance.dismiss();
						$scope.loadingInProgress = false;
						showAlert($scope.msg);
					}
				}, function err(httpResponse) {
					//$scope.modalInstance.dismiss();
					$scope.loadingInProgress = false;
					showAlert($scope.msg);
				}
		);
	};

	//loading();
	var deviceName = $location.search().name;
	var deviceId = $location.search().id;
	if (isNotDefined(deviceId)){
		// No device name provided, show the default?
		getDefaultDeviceInfo();
	} else {
		// Get the device info
		getDriveDeviceInfo(deviceId);
	}
	
	// TODO - Check device status
	// $scope.postGTalkAsyncAction('status', 'get');
	
	// TODO Start status checking
	// $scope.statusChecking();
		
}).controller( 'DeviceInfoModalCtrl', function DeviceInfoModalCtrl($rootScope, $scope, $uibModalInstance, deviceInfo) {
	$scope.deviceInfo = deviceInfo;
	$scope.close = function () {
		$uibModalInstance.close($scope.deviceInfo);
	};	
}).controller( 'MsgModalCtrl', function MsgModalCtrl($rootScope, $scope, $uibModalInstance, msg) {
	$scope.msg = msg;
	$scope.close = function () {
		$uibModalInstance.close(msg);
	};	
}).controller('LoadingModalCtrl', function LoadingModalCtrl($rootScope, $scope, $uibModalInstance) {
	
}).controller('ProgressModalCtrl', function ProgressModalCtrl($rootScope, $scope, $uibModalInstance, progressLevel) {
	$scope.progressLevel = progressLevel;
	$scope.progressLevel = 50;
}).directive('errSrc', function() {
    return {
        link: function(scope, element, attrs) {
            element.bind('error', function() {
                if (attrs.src != attrs.errSrc) {
                    attrs.$set('src', attrs.errSrc);
                }
            });
        }
    };
}).filter('startFrom', function() {
    return function(input, start) {
        if(input) {
            start = +start; //parse to int
            return input.slice(start);
        }
        return [];
    };
}).controller( 'ViewerModalCtrl', function ViewerModalCtrl(
	$rootScope, $scope, $window, $uibModal, $http, $location, 
	$interval, $localStorage, $log, $uibModalInstance, slides) {
	

    $scope.rowSize = 3; // number of images in a row
    $scope.entryLimit = 12; // max number of items to display in a page
    $scope.currentPage = 1;
    $scope.maxSize = 7; // max number of pagination items
    $scope.defaultImg = "assets/images/placeholder.png";
    var listImg = []; // all images from server
    var searchedImg = [];
    var start = true;
    var timer;
    var storage = $localStorage;

    /*
    $http.get(imagesJson).success( function (data) {
        data = $scope.removeDeleted(data, storage);
        listImg = data;
        searchedImg = listImg;
        initPagination(listImg);
    }).error( function (error) {
        console.error(error);
    });
    */

    $scope.setPage = function (pageNumber) {
        $scope.currentPage = pageNumber;
    };

    $scope.sortBy = function(predValue) {
        var predicate = predValue.substring(0, predValue.length - 2);
        var data = [];
        if( predValue[predValue.length - 1] == "↑") {
            data = (predicate == "title")? searchedImg.sort(increasSortTitle) : searchedImg.sort(increasSortDate);
        } else {
            data = (predicate == "title")? searchedImg.sort(sortTitle) : searchedImg.sort(sortDate);
        }
        initPagination(data);
    };

    $scope.search = function(searchStr) {
        var searchText = searchStr.toLowerCase();
        searchedImg = listImg.filter(
            function isFiltered(element) {
                return (element.title.toLowerCase().indexOf(searchText) !== -1 || element.date.toLowerCase().indexOf(searchText) !== -1);
            }
        );
        initPagination(searchedImg);
    };

    $scope.openImg = function(img) {
        $scope.show = true;
        $scope.currImg = img||searchedImg[0];
    };

    $scope.closeImg = function() {
        $scope.show = false;
        if(start === false && angular.isDefined(timer)) {
            $interval.cancel(timer);
            timer = undefined;
        }
    };

    $scope.isDeleted = function(id) {
        var deleted = false;
        if (storage && storage.deleted) {
            deleted = (storage.deleted.indexOf(id) !== -1);
        }

        return deleted;
    };

    $scope.removeDeleted = function(images) {
        for(var i = 0, length = images.length; i < length; i++) {
            if ($scope.isDeleted(images[i].title)) {
                images.splice(i, 1);
                length--;
            }
        }


        return images;
    };

    $scope.deleteByStorage = function(id) {
        if (!storage.deleted) {
            storage.deleted = [];
        }
        storage.deleted.push(id);
    };

    $scope.deleteImg = function(img) {
        var confirmMsg = "Do you want to delete '" + img.title + "'?";
        var confirmed = confirm(confirmMsg);
        if (confirmed) {
            $scope.deleteByStorage(img.title);
            listImg = $scope.removeDeleted(listImg);
            searchedImg = $scope.removeDeleted(searchedImg);
            initPagination(searchedImg);
        }
    };

    $scope.nextImg = function() {
        var index = searchedImg.indexOf($scope.currImg);
        $scope.currImg = searchedImg[++index % searchedImg.length];
    };

    $scope.prevImg = function() {
        var index = searchedImg.indexOf($scope.currImg);
        index = (index > 0)? --index : searchedImg.length - 1;
        $scope.currImg = searchedImg[index];
    };

    $scope.startSlideshow = function () {
        if (start) {
            start = false;
            var speed = +$scope.speed || 2000;
            if(!$scope.show){
                $scope.openImg();
            }
            timer = $interval($scope.nextImg, speed, searchedImg.length);

        } else {
            start = true;
            if (angular.isDefined(timer)) {
                $interval.cancel(timer);
                timer = undefined;
            }
            $scope.closeImg();
        }
    };

    function initPagination (data) {
        $scope.totalItems = data.length;
        var rowsCount = Math.ceil(data.length / $scope.rowSize);
        var index;
        $scope.imagesData = [];
        for (var i = 0; i < rowsCount; i++) {
            $scope.imagesData[i] = [];
            for (var j = 0; j < $scope.rowSize; j++) {
                index = $scope.rowSize * i + j;
                if (data[index]) {
                  $scope.imagesData[i][j] = data[index];
                }
            }
        }
    }

    function increasSortTitle(a, b) {
        return (a.title < b.title)? -1 : (a.title > b.title)? 1 : 0;
    }

    function sortTitle(a, b) {
        return (a.title > b.title)? -1 : (a.title < b.title)? 1 : 0;
    }

    function increasSortDate(a, b) {
        return Date.parse(a.date) - Date.parse(b.date);
    }

    function sortDate(a, b) {
        return Date.parse(b.date) - Date.parse(a.date);
    }


	/*
	$scope.menuDisplay = "none";
	$scope.toggleViewerMenu = function(){
		if ($scope.menuDisplay == "none") {
			$scope.menuDisplay = "block";
		} else {
			$scope.menuDisplay = "none";
		}
	};
	$scope.navigateToImage = function (idx) {
		$scope.slides[idx].active = true;
	};
	
	$scope.slides = slides;
	*/

	$scope.close = function () {
		$uibModalInstance.close($scope.slides);
	};


	// Start up code
	slides = $scope.removeDeleted(slides, storage);
    listImg = slides;
    searchedImg = listImg;
    initPagination(listImg);

})
;


		