angular.module('resources.streaming', [ 'ngResource' ]);
angular.module('resources.streaming').factory(
		'Streaming',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var streamingService = {};
					
					var s4 = function(){
						return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
					};
					
					var guid = function() {
						return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
						s4() + '-' + s4() + s4() + s4();
					};
					
					
					streamingService.getResolutions = function() {

						if ($rootScope.debug_mode) {
							return $resource('assets/json/resolution.json', {},
									{
										get : {
											method : 'GET',
											isArray : true
										}
									});
						} else {
							var path = $rootScope.surveillanceUrl + "/processor/query?type=0" + '&id=' + guid();
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : true
										}
									});
						}

					};
					streamingService.getCanvasSizes = function() {
							return $resource('assets/config/canvas_size.json', {},
									{
										get : {
											method : 'GET',
											isArray : true
										}
									});
					};
					
					streamingService.getSceneModes = function() {
							var path = $rootScope.surveillanceUrl + "/processor/query?type=1" + '&id=' + guid();
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : true
										}
									});
					};
					
					streamingService.getColorEffects = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=2" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : true
									}
								});

					};
					
					streamingService.getFlashModes = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=4" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : true
									}
								});

					};
					
					streamingService.getFocusModes = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=5" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : true
									}
								});

					};
					
					streamingService.getWhiteBalance = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=6" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : true
									}
								});

					};
					
					streamingService.getAntibanding = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=7" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : true
									}
								});

					};
					
					streamingService.getExposureCompensation = function() {
						var path = $rootScope.surveillanceUrl + "/processor/query?type=9" + '&id=' + guid();
						return $resource(path, {},
								{
									get : {
										method : 'GET',
										isArray : false
									}
								});

					};
					
					streamingService.setResolution = function(resolution, scope){
							var path = $rootScope.surveillanceUrl + "/processor/setup?width=" + resolution.width + "&height="+ resolution.height + '&id=' + guid();
							$http({method: 'GET', url: path}).
								success(function(data, status, headers, config) {
									scope.imageWidth = scope.resolutionValue.width;
									scope.imageHeight = scope.resolutionValue.height;
									if (scope.streamingMethod != 'js') {
										scope.videoSrc.width = scope.imageWidth * (scope.data.screenSize / 100);
										scope.videoSrc.height = scope.imageHeight * (scope.data.screenSize/ 100);
										
										//scope.videoSrc.width = scope.imageWidth * (scope.canvasSize.ratio / 100);
										//scope.videoSrc.height = scope.imageHeight * (scope.canvasSize.ratio / 100);
									}
								}).
								error(function(data, status, headers, config) {
									
								});							
					};
					return streamingService;
				} ]);