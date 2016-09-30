angular.module(
	'mymobkit.surveillance',
[ 
	'ui.router', 
	'ui.router.state', 
	'placeholders', 
	'ui.bootstrap', 
	'resources.streaming',
	'directives.video', 
	'directives.image', 
	'directives.audio',
	'ngAnimate',
	'toaster'
])
.config(["$stateProvider", "$sceDelegateProvider", "$httpProvider", function config($stateProvider, $sceDelegateProvider, $httpProvider) {
	$stateProvider.state('surveillance', {
		url : '/surveillance',
		views : {
			"main" : {
				controller : 'SurveillanceCtrl',
				templateUrl : 'surveillance/surveillance.tpl.html'
			}
		},
		data : {
			pageTitle : 'Surveillance'
		}
	});
	
	$sceDelegateProvider.resourceUrlWhitelist(['.*']);
}])
.filter('resFilter', function() {
	return function(input, scope) {	// We passed in scope here, you can also pass in a scope variable
		if (input.width == scope.defaultRes.width && input.height == scope.defaultRes.height) {
			scope.resolutionValue = input;
		}
		return (input.width + ' x ' + input.height);
    };
})
/*
.filter('sizeFilter', function() {
	return function(size, scope) {	// We passed in scope here, you can also pass in a scope variable
		if (size.ratio == scope.defaultCanvasSize.ratio) {
			scope.canvasSize = size;
		}
		return (size.ratio + '%');
    };
})
*/
.controller(
	'SurveillanceCtrl',
	["$rootScope", "$scope", "$http", "$location", "Streaming", "$window", "$timeout", "$resource", "$log", "toaster", "$state", function SurveillanceCtrl($rootScope, $scope, $http, $location, Streaming,	$window, $timeout, $resource, $log, toaster, $state) {
		$scope.isShutdownCamera = false;
		$scope.toLockCamera = false;
		$scope.imageWidth = 0;
		$scope.imageHeight = 0;
		$scope.playControlFlag = 'ok';	
		$scope.rotationDegree = 360;
		$scope.videoPanelFiller = 0;
		$scope.data = {imageQuality:50, zoomLevel:0, screenSize:100};
		$scope.windowDimension = {width: $window.innerWidth, height: $window.innerHeight};
		$scope.exposureCompensation = {min:-1, max:-1, current:-1};
		$scope.autoResizeScreen = true;
		$scope.recordingStatus = {fileName:'', isRecording:'false'};
		$scope.videoPrefix = '';
		var recordingStatusTimer = null;
		var videoTimer = null;
		$scope.inStreaming = false;
		$scope.videoFrameCount = 0;
		$scope.mediaInfo = false;
		$scope.videoSrc = {path: 'assets/images/white.png', width: 0, height: 0, displayStyle:'' };
	
		//$scope.nightVisionSwitch = false;
		$scope.histogram = true;
		$scope.gamma = false;
		$scope.nightVisionStatus = 'Night vision is off';
		$scope.gammaLevel = 1;

		$scope.motionDetection = true;
		$scope.motionDetectionThreshold = 80;
		$scope.histogramEqualizationOption = 'Default';

		$scope.msg =  {
				title: 'Alert',
				content: 'Unable to send command to device.'
		};

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
		
		var showAlert = function (obj) {
			toaster.pop('warning', obj.title, obj.content);
		};
	
		var showMsg = function (obj) {
			toaster.pop('success', obj.title, obj.content);
		};

		var getResolutions = function(){
			// Get list of resolutions
			$scope.debugMsg = 'Retrieving resolutions...';
			Streaming.getResolutions().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.defaultRes = data[0];
							$scope.resolutions = data;
							$scope.resolutions.splice(0,1);
						}
						$scope.debugMsg = '';
						$scope.audioSrc = '';
						$scope.autoPlay = false;
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		$scope.showSnapshot = function() {
			$window.open($scope.surveillanceUrl  + '/video_stream/live.jpg?id=' + guid());
		};
		
		$scope.showFullScreen = function() {
			//$log.info($state.href("fullscreen", null, null));
			$window.open($state.href("fullscreen", null, null));
		};
				
		$scope.takePhoto = function() {
			$scope.debugMsg = 'Status: Sending command to device.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=22&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Photo command sent to device.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to send photo command to device.";
			});
		};

		/*
		var getCanvasSizes = function(){
			$scope.debugMsg = 'Retrieving canvas sizes...';
			Streaming.getCanvasSizes().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.defaultCanvasSize = data[0];
							$scope.canvasSizes = data;
							$scope.canvasSizes.splice(0,1);
							$scope.canvasSize = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						//$scope.debugMsg =  "Failed to connect to device. Try refreshing the page.";
					});
		};
		*/
		
		var getSceneModes = function(){
			$scope.debugMsg = 'Retrieving scene modes...';
			Streaming.getSceneModes().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.sceneModes = data;
							$scope.sceneMode = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getColorEffects = function(){
			$scope.debugMsg = 'Retrieving color effects...';
			Streaming.getColorEffects().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.colorEffects = data;
							$scope.colorEffect = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getFlashModes = function(){
			$scope.debugMsg = 'Retrieving flash modes...';
			Streaming.getFlashModes().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.flashModes = data;
							$scope.flashMode = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getFocusModes = function(){
			$scope.debugMsg = 'Retrieving focus modes...';
			Streaming.getFocusModes().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.focusModes = data;
							$scope.focusMode = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getWhiteBalance = function(){
			$scope.debugMsg = 'Retrieving white balance...';
			Streaming.getWhiteBalance().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.whiteBalances = data;
							$scope.whiteBalance = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		
		var getAntibanding = function(){
			$scope.debugMsg = 'Retrieving antibanding...';
			Streaming.getAntibanding().query().$promise.then(
					function success(data, headers) {
						if (angular.isArray(data)) {
							$scope.antibandings = data;
							$scope.antibanding = data[0];
						}
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getExposureCompensation = function(){
			$scope.debugMsg = 'Retrieving exposure compensation...';
			Streaming.getExposureCompensation().get().$promise.then(
					function success(data, headers) {
						$scope.exposureCompensation.min = data.min;
						$scope.exposureCompensation.max = data.max;
						$scope.exposureCompensation.current = data.current;
						$scope.debugMsg = '';
					}, function err(httpResponse) {
						$scope.debugMsg = '';
					});
		};
		
		var getImageQuality = function(){
			$scope.debugMsg = 'Retrieving image quality...';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=3' + '&id=' + guid()}).
			success(function(data, status, headers, config) {
				$scope.data.imageQuality = parseInt(data, 10);
				$scope.debugMsg = '';
			}).
			error(function(data, status, headers, config) {
				$scope.debugMsg = '';
			});
		};

		var getMotionDetectionThreshold = function(){
			$scope.debugMsg = 'Retrieving motion detection threshold...';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=13' + '&id=' + guid()}).
			success(function(data, status, headers, config) {
				$scope.motionDetectionThreshold = parseInt(data, 10);
				$scope.debugMsg = '';
			}).
			error(function(data, status, headers, config) {
				$scope.debugMsg = '';
			});
		};
		
		var getAutoExposureLock = function(){
			$scope.debugMsg = 'Retrieving auto exposure lock...';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=8' + '&id=' + guid()}).
			success(function(data, status, headers, config) {
				if (data.indexOf('true') > -1) {
					$scope.autoExposureLock = true;
				} else {
					$scope.autoExposureLock = false;
				}
				$scope.debugMsg = '';
			}).
			error(function(data, status, headers, config) {
				$scope.debugMsg = '';
			});
		};

		var getMotionDetectionMode = function(){
			$scope.debugMsg = 'Retrieving motion detection mode...';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=11' + '&id=' + guid()}).
			success(function(data, status, headers, config) {
				if (data.indexOf('true') > -1) {
					$scope.motionDetection = true;
				} else {
					$scope.motionDetection = false;
				}
				$scope.debugMsg = '';
			}).
			error(function(data, status, headers, config) {
				$scope.debugMsg = '';
			});
		};


		var getNightVisionMode = function(){
			$scope.debugMsg = 'Retrieving night vision mode...';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=12' + '&id=' + guid()}).
			success(function(data, status, headers, config) {
				if (data.indexOf('true') > -1) {
					$scope.nightVisionSwitch = true;
					$scope.nightVisionStatus = 'Night vision is on';
				} else {
					$scope.nightVisionSwitch = false;
					$scope.nightVisionStatus = 'Night vision is off';
				}
				$scope.debugMsg = '';
			}).
			error(function(data, status, headers, config) {
				$scope.debugMsg = '';
			});
		};
		
		
		$scope.selectRes = function() {
			$scope.defaultRes = $scope.resolutionValue;
			Streaming.setResolution($scope.resolutionValue, $scope);	
			$scope.setVideoPanelSize();	
		};
		
		
		$scope.selectSceneMode = function() {
			$scope.debugMsg = 'Status: Changing scene mode.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=9&value=' + $scope.sceneMode + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Scene mode changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change scene mode.";
			});
		};
		
		
		$scope.selectColorEffect = function() {
			$scope.debugMsg = 'Status: Changing color effect.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=10&value=' + $scope.colorEffect + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Color effect changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change color effect.";
			});
		};
		
		$scope.selectFlashMode = function() {
			$scope.debugMsg = 'Status: Changing flash mode.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=13&value=' + $scope.flashMode + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Flash mode changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change flash mode.";
			});
		};
		
		$scope.selectFocusMode = function() {
			$scope.debugMsg = 'Status: Changing focus mode.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=14&value=' + $scope.focusMode + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Focus mode changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change focus mode.";
			});
		};
		
		
		$scope.selectWhiteBalance = function() {
			$scope.debugMsg = 'Status: Changing white balance.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=15&value=' + $scope.whiteBalance + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: White balance changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change white balance.";
			});
		};
		
		
		$scope.selectAntibanding = function() {
			$scope.debugMsg = 'Status: Changing antibanding.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=16&value=' + $scope.antibanding + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Antibanding changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change antibanding.";
			});
		};
		
		var setStreamingMethod = function() {
			var agent = $window.navigator.userAgent;
			var msie = agent.indexOf('MSIE ');
			var trident = agent.indexOf('Trident/');
			
			if (msie > -1 || trident > -1) {
				$scope.streamingMethod = 'js';
			} else {
				$scope.streamingMethod = 'mjpg';
			}
			
			//if (agent.toLowerCase().indexOf('chrome') > -1 || agent.toLowerCase().indexOf('firefox') > -1){
			//	$scope.streamingMethod = 'mjpg';
			//} else {
			//	$scope.streamingMethod = 'js';
			//}
		};

		setStreamingMethod();
		
		$scope.frameStreamingVideo = function(){
			if (!$scope.inStreaming) {
				return;
			}
			videoTimer = $timeout(
					function() {
						var imgPath = $scope.surveillanceUrl  + '/video_stream/live.jpg?id=' + guid();
						var img = new Image();
						img.src = imgPath;
						img.onload = function () {
							var resizeRequired = ($scope.imageWidth === 0);
							$scope.imageWidth = img.width;
							$scope.imageHeight = img.height;
							if (resizeRequired) {
								$scope.resizeVideoPanel();
							}
							$scope.$apply(function() {
								$scope.videoSrc.path = imgPath;
								$scope.videoSrc.width = img.width * ($scope.data.screenSize / 100);
								$scope.videoSrc.height = img.height * ($scope.data.screenSize / 100);
								});
							$scope.frameStreamingVideo();
							};						
					}, 
			300);
		};
		
		$scope.mjpgStreamingVideo = function(){
			$scope.videoSrc.path = $scope.surveillanceUrl  + '/video/live.mjpg?id=' + guid();
			var img = new Image();
			img.src = $scope.videoSrc.path;
			img.onload = function () {
				var resizeRequired = ($scope.imageWidth === 0);
				$scope.imageWidth = img.width;
				$scope.imageHeight = img.height;
				if (resizeRequired) {
					$scope.resizeVideoPanel();
				}
				$scope.$apply(function() {
					$scope.videoSrc.width = img.width * ($scope.data.screenSize / 100);
					$scope.videoSrc.height = img.height * ($scope.data.screenSize / 100);
					});
				};
		};
		
		$scope.playClick = function($event) {
			var btn = $event.target;
			
			if (!$scope.inStreaming) {				
				$scope.startStreaming();					
			} else {
				$scope.stopStreaming();				
				// Check if need to shutdown camera
				if ($scope.isShutdownCamera) {
					$scope.turnOffCameraActivity();
				} 
			}
		};
		
		$scope.initializeStream = function(){
			
			if ($scope.streamingMethod == 'js') {
				$scope.frameStreamingVideo();	
			} else {
				$scope.mjpgStreamingVideo();
			}

			// Check heartbeat
			
			if (isNotDefined($scope.resolutionValue)) {
				getResolutions();
			}
			
			if (isNotDefined($scope.sceneModes)) {
				getSceneModes();
			}
			
			if (isNotDefined($scope.colorEffects)) {
				getColorEffects();
			}
			
			if (isNotDefined($scope.flashModes)) {
				getFlashModes();
			}
			
			if (isNotDefined($scope.focusModes)) {
				getFocusModes();
			}
			
			if (isNotDefined($scope.whiteBalances)) {
				getWhiteBalance();
			}
			
			if (isNotDefined($scope.antibandings)) {
				getAntibanding();
			}
			
			if ($scope.exposureCompensation.max == -1) {
				getExposureCompensation();
			}
			
			if (isNotDefined($scope.autoExposureLock)) {
				getAutoExposureLock();
			}

			if (isNotDefined($scope.motionDetection)) {
				getMotionDetectionMode();
			}

			if (isNotDefined($scope.nightVisionSwitch)) {
				getNightVisionMode();
			}
			
			getImageQuality();
			getMotionDetectionThreshold();
			
			$scope.playControlFlag = 'ok';
			
			// Check if need to lock camera					
			if ($scope.toLockCamera) {
				$scope.lockScreen();
			}		
			
			$scope.getRecordingStatus();
			
			$scope.debugMsg = 'Status: Connected to device.';
		};
		
		$scope.startStreaming = function(){
			$scope.inStreaming = true;
			$scope.playControlFlag = '';
			$scope.debugMsg = 'Status: Connecting to device.';
			
			var heartBeat = $http({method: 'GET', url: $rootScope.host  + '/services/surveillance/status?id=' + guid()});
			heartBeat.
				success(function(data, status, headers, config) {
					if (data.indexOf("OK") > -1) { 
						// Is already in video streaming mode
						$scope.initializeStream();
					} else {
						$http({method: 'GET', url: $rootScope.host  + '/services/surveillance/start?id=' + guid()}).
						success(function(data, status, headers, config) {
							$timeout(function(){$scope.initializeStream();}, 2000);
						}).
						error(function(data, status, headers, config) {
							$scope.debugMsg =  "Status: Failed to connect to device. Try refreshing the page.";
							$scope.playControlFlag = 'ok';
							$scope.inStreaming = false;
						});
					}
				}).
				error(function(data, status, headers, config) {
					$http({method: 'GET', url: $rootScope.host  + '/services/surveillance/start?id=' + guid()}).
						success(function(data, status, headers, config) {
							$timeout(function(){$scope.initializeStream();}, 2000);
						}).
						error(function(data, status, headers, config) {
							$scope.debugMsg =  "Status: Failed to connect to device. Try refreshing the page.";
							$scope.playControlFlag = 'ok';
							$scope.inStreaming = false;
					});
			});		
			//});
			
		};
		
		$scope.$on('$destroy', function(){
			$scope.stopStreaming();
		});
		
		
		$scope.stopStreaming = function(){
			$scope.inStreaming = false;
			$scope.playControlFlag = '';
			if ($scope.streamingMethod == 'js') {
				if (!$timeout.cancel(videoTimer)){
				}
			} else {
				$scope.videoSrc.path = ' ';
				//var img = new Image();
				//img.src = $scope.videoSrc.path;
			}
			$scope.debugMsg = '';
			$scope.playControlFlag = 'ok';
			if (!$timeout.cancel(recordingStatusTimer)){
			}
		};
		
		$scope.showMediaInfo = function(){
			$scope.mediaInfo = !$scope.mediaInfo;
		};
		
		$scope.toggleCamera = function(){
			$scope.debugMsg = 'Status: Toggle device camera.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=1&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Toggled device camera.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to toggle device camera. Try refreshing the page.";
			});
		};
		
		$scope.rotateImage = function(){	
			$scope.rotationDegree += 90;
			if ($scope.rotationDegree > 360) {
				$scope.rotationDegree = 90;
			}
			$scope.setVideoPanelSize();		
		};
		
		$scope.setVideoPanelSize = function(){
			$scope.videoPanelFiller = ($scope.rotationDegree == 90 || $scope.rotationDegree == 270) ? ( ($scope.videoSrc.width - $scope.videoSrc.height) / 2) :0;	
		};
		
		$scope.toggleMotion = function(){
			$scope.debugMsg = 'Status: Toggle device motion viewing.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=2&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Toggled device motion viewing.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to toggle device motion viewing. Try refreshing the page.";
			});
		};
		
		$scope.turnOffCameraActivity = function(){
			$scope.playControlFlag = '';	
			$scope.debugMsg = 'Status: Shutting down device camera.';
			
			$http({method: 'GET', url: $rootScope.host  + '/services/surveillance/shutdown?id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Device camera is shut down.';
					$scope.playControlFlag = 'ok';	
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to shut down device camera. Try refreshing the page.";
					$scope.playControlFlag = 'ok';	
			});
			
			/*
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=0&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Device camera is shut down';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to shut down device camera. Try refreshing the page.";
			});
			*/
		};
		
		$scope.lockScreen = function(){
			$scope.playControlFlag = '';	
			$scope.debugMsg = 'Status: Locking camera.';
			
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=4&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Camera is locked.';
					$scope.playControlFlag = 'ok';	
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to lock camera. Try refreshing the page.";
					$scope.playControlFlag = 'ok';	
			});
		};
		
		/*
		$scope.selectCanvasSize = function() {
			$scope.defaultCanvasSize = $scope.canvasSize;
			if ($scope.streamingMethod != 'js') {
				$scope.videoSrc.width = $scope.imageWidth * ($scope.canvasSize.ratio / 100);
				$scope.videoSrc.height = $scope.imageHeight * ($scope.canvasSize.ratio / 100);
			}
			
			$scope.setVideoPanelSize();	
			// $scope.changeVideoPaneSize($scope.getWindowDimensions(), null);
		};
		*/
		
		$scope.selectScreenSize= function() {
			$scope.resizeScreen();
		};
		
		$scope.resizeScreen = function(){
			var screenSize = $scope.data.screenSize;
			if ($scope.streamingMethod != 'js') {
				$scope.videoSrc.width = $scope.imageWidth * ($scope.data.screenSize / 100);
				$scope.videoSrc.height = $scope.imageHeight * ($scope.data.screenSize/ 100);
			}
			$scope.setVideoPanelSize();	
		};
		
		
		$scope.selectAudio = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			if (isChecked){
				$scope.audioSrc = $scope.surveillanceUrl  + '/audio_stream/live.mp3?id=' + guid();
				$scope.autoPlay = true;
			} else {
				$scope.audioSrc = '';
				$scope.autoPlay = false;
			}
		};
		
		$scope.shutdownCamera = function($event) {
			var checkbox = $event.target;
			$scope.isShutdownCamera = checkbox.checked;
		};
		
		$scope.lockCamera = function($event) {
			var checkbox = $event.target;
			$scope.toLockCamera = checkbox.checked;
		};
		
		$scope.selectLED = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Toggle flash.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=3&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Toggled flash.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to toggle flash. Try refreshing the page.";
			});
		};
		
		$scope.disguiseCamera = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Toggle camera disguise.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=8&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Toggled camera disguise.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to toggle camera disguise. Try refreshing the page.";
			});
		};
		
		$scope.startRecording = function() {
			$scope.debugMsg = 'Status: Start video recording.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=19&value=' + $scope.videoPrefix + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = '';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to start video recording.";
			});
		};
		
		$scope.stopRecording = function() {
			$scope.debugMsg = 'Status: Stop video recording.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=20&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = '';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to stop video recording.";
			});
		};
		
		$scope.selectAutoResizeScreen = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
		};
		
		$scope.selectAutoExposureLock = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing auto exposure lock.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=17&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Auto exposure lock changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change auto exposure lock.";
			});
		};

		$scope.selectMotionDetection = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing motion detection mode.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=27&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Motion detection mode changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change motion detection mode.";
			});
		};
		
		$scope.selectImageQuality = function() {
			$scope.debugMsg = 'Status: Changing image quality.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=12&value=' + $scope.data.imageQuality + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Image quality changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change image quality.";
			});
		};

		$scope.selectMotionDetectionThreshold = function() {
			$scope.debugMsg = 'Status: Changing motion detection threshold.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=28&value=' + $scope.motionDetectionThreshold + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Motion detection threshold changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change motion detection threshold.";
			});
		};

		$scope.selectGammaLevel = function() {
			$scope.debugMsg = 'Status: Changing gamma level.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=26&value=' + $scope.gammaLevel + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Gamma level changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change gamma level.";
			});
		};
		
		$scope.selectZoom = function() {
			$scope.debugMsg = 'Status: Changing zoom level.';
			var zoomLevel = $scope.data.zoomLevel;
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=11&value=' + zoomLevel + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Zoom level changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change zoom level.";
			});
		};
		
		$scope.selectExposureCompensation = function() {
			$scope.debugMsg = 'Status: Changing exposure compensation.';
			var es = $scope.exposureCompensation.current;
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=18&value=' + es + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Exposure compensation changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change exposure compensation.";
			});
		};
		
		$scope.selectVideoControl = function() {
			$scope.videoControl = !$scope.videoControl;
		};
		
		$scope.selectCameraSettings = function() {
			$scope.cameraSettings = !$scope.cameraSettings;
		};
		
		$scope.selectVideoSettings = function() {
			$scope.videoSettings = !$scope.videoSettings;
		};

		$scope.selectNightVisionSettings = function() {
			$scope.nightVisionSettings = !$scope.nightVisionSettings;
		};


		$scope.enableHistogramEqualization = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing night vision display.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=23&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Night vision display is changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change night vision display. Try refreshing the page.";
			});
			
		};


		$scope.selectHistogramEqualization = function() {
			var onOff = 'true';
			if ($scope.histogramEqualizationOption === 'Default'){
				onOff = 'false';
			} 
			
			$scope.debugMsg = 'Status: Changing night vision display option.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=25&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Night vision display option is changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change night vision display option. Try refreshing the page.";
			});
			
		};

		$scope.enableGammaCorrection = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing night vision display.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=24&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Night vision display is changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change night vision display. Try refreshing the page.";
			});
		};

		$scope.enableColorHistogramEqualization = function($event) {
			var checkbox = $event.target;
			var isChecked = checkbox.checked;
			
			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing night vision display.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=25&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Night vision display is changed.';
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change night vision display. Try refreshing the page.";
			});
		};

		$scope.selectNightVision = function($event){
			var checkbox = $event.target;
			var isChecked = checkbox.checked;

			var onOff = 'false';
			if (isChecked){
				onOff = 'true';
			} 
			
			$scope.debugMsg = 'Status: Changing night vision mode.';
			$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/features?feature=21&value=' + onOff + '&id=' + guid()}).
				success(function(data, status, headers, config) {
					$scope.debugMsg = 'Status: Night vision mode is changed.';
					if (isChecked) {
						$scope.nightVisionStatus = 'Night vision is on';
					} else {
						$scope.nightVisionStatus = 'Night vision is off';
					}
				}).
				error(function(data, status, headers, config) {
					$scope.debugMsg =  "Status: Failed to change night vision mode. Try refreshing the page.";
					$scope.nightVisionSwitch = !$scope.nightVisionSwitch;
			});			
		};
		
		$scope.resizeVideoPanel = function() {
			var padding = 40;
			var optimumWidth = $scope.windowDimension.width - (padding * 2);
			if (optimumWidth < 0) {
				$scope.data.screenSize = 10;
			} else {
				$scope.data.screenSize = Math.round((optimumWidth / $scope.imageWidth ) * 100);
			}
			
			if ($scope.data.screenSize > 100) {
				$scope.data.screenSize = 100;
			}
			
			$scope.resizeScreen();
		};
		
		
		$scope.getRecordingStatus = function(){
			if (!$scope.inStreaming) {
				return;
			}
			recordingStatusTimer = $timeout(
					function() {
						$http({method: 'GET', url: $scope.surveillanceUrl   + '/processor/query?type=10' + '&id=' + guid()}).
						success(function(data, status, headers, config) {
							$scope.recordingStatus.fileName = data.fileName;
							$scope.recordingStatus.isRecording = data.isRecording;
							if ($scope.recordingStatus.isRecording.indexOf('true') > -1) {
								$scope.debugMsg = 'Video recording in progress. Preview is not available.';
								$scope.videoSettings = true;
							} else {
								$scope.debugMsg = '';
							}
							$scope.getRecordingStatus();
						}).
						error(function(data, status, headers, config) {
							$scope.getRecordingStatus();
						});		
					}, 
			3000);
		};
		
		
		$(window).resize(function(){
			$scope.$apply(function(){
				if ($scope.imageWidth > 0 && $scope.autoResizeScreen) {
					$scope.windowDimension.width = $window.innerWidth;
					$scope.resizeVideoPanel();
				}
			});
		});
		
		if (!$rootScope.debug_mode) {
			var surveillance = $resource($rootScope.host + "/services/surveillance/url?id=" + guid());
			var obj  = surveillance.get(function() {
				$scope.surveillanceUrl = obj.url;				
				$rootScope.surveillanceUrl = obj.url;
				
				getResolutions();
				getSceneModes();
				getColorEffects();
				getImageQuality();
				getMotionDetectionThreshold();
				getFlashModes();
				getFocusModes();
				getWhiteBalance();
				getAntibanding();
				getExposureCompensation();
				getAutoExposureLock();
				getMotionDetectionMode();
				getNightVisionMode();
			});
		} else {			
			$scope.surveillanceUrl = $rootScope.surveillance_mock_url;			
			$rootScope.surveillanceUrl = $rootScope.surveillance_mock_url;
			
			getResolutions();
			getSceneModes();
			getColorEffects();
			getImageQuality();
			getMotionDetectionThreshold();
			getFlashModes();
			getFocusModes();
			getWhiteBalance();
			getAntibanding();
			getExposureCompensation();
			getAutoExposureLock();
			getMotionDetectionMode();
			getNightVisionMode();
		}		
	}]
)
;