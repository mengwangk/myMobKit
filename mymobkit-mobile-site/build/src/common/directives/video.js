angular.module('directives.video', [])
.directive('videoplayer', ["$window", function($window) {
	return { 
		restrict: 'EA',
		replace: false,
		template: '<img ng-style="{width: videoSrc.width + \'px\', height: videoSrc.height + \'px\' }" ng-src="{{videoSrc.path}}" />',
		scope: {
			videoSrc: '='
		},
		link: function(scope, element, attrs) {

			var updateVideoSrc = function(){
				var img = new Image();
				img.src = scope.videoSrc.path;
				img.onload = function () {
					scope.$apply(function() {
						scope.videoSrc.width = img.width;
						scope.videoSrc.height = img.height;
						});
					};
				img.src = scope.videoSrc.path;
			};
				
			scope.$watch('videoSrc', function(oldVal, newVal) {
				if (newVal) {
					updateVideoSrc();
				}
			});
		
			
		/*scope.getWindowDimensions = function() {
			return {
				'h' : $window.innerHeight,
				'w' : $window.innerWidth
			};
		};
		
		scope.changeVideoPaneSize = function(newValue, oldValue) {
			scope.windowHeight = newValue.h;
			scope.windowWidth = newValue.w;
			var ratio = 70;
			if (scope.canvasSize) {
				ratio = scope.canvasSize.ratio;
			}
			scope.style = function() {
				return {
					'height' : (newValue.h * (ratio / 100) ) + 'px'
				};
			};
		};
		scope.$watch(scope.getWindowDimensions, scope.changeVideoPaneSize , true);
		angular.element($window).bind('resize', function() {
			scope.$apply();
		});*/
	}
  };
}]);