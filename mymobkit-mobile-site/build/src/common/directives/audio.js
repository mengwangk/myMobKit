angular.module('directives.audio', [])

.directive('jplayer', ["$window", function($window) {
	return {
	restrict: 'EA',
	template: '<div></div>',
	link: function(scope, element, attrs, window) {
		scope.updatePlayer = function() {
			var autoplay = scope.$eval(attrs.autoplay);
			var agent = $window.navigator.userAgent;
			var mediaUrl = scope.$eval(attrs.audio);
			if  (typeof(mediaUrl) === 'undefined' || mediaUrl == null || mediaUrl ==='') {
				return;
			}
			var whatToUse = "flash, html";
			//if (agent.indexOf("Trident/5") > -1 || agent.toLowerCase().indexOf('firefox') > -1){
			//	whatToUse = "flash, html";
			//}
			$(element).jPlayer(
					{
						//errorAlerts: true,
						swfPath : 'assets/jplayer',
						supplied : 'mp3',
						solution : whatToUse,
						wmode : 'window',
						smoothPlayBar : true,
						keyEnabled : true,
						preload: 'auto',
						autoBlur: false,
						useStateClassSkin: true,
						remainingDuration: true,
						toggleDuration: true,
						ready : function() {
							$(element).jPlayer("setMedia", {
								mp3 : mediaUrl
							});
							var action = (autoplay === true ? 'play' : 'stop');
							$(element).jPlayer(action);
						}							
					}).end();
		};
		
		scope.$watch(attrs.audio, scope.updatePlayer);
		scope.updatePlayer();
		
	}
  };
}]);