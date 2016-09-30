angular.module('directives.scrollglue', [])
.directive('scrollglue', function($window) {
   return {
    restrict: 'A',
    link: function(scope, element, attrs) {
        if (scope.$last) {
			scope.scrolltoEnd();
		}
    }
  };
});