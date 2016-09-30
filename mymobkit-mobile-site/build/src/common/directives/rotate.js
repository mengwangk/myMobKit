angular.module('directives.image', [])
.directive('rotate', function() {
    return {
        link: function(scope, element, attrs) {
            // watch the degrees attribute, and update the UI when it changes
            scope.$watch(attrs.degrees, function(rotateDegrees) {
                //transform the css to rotate based on the new rotateDegrees
                element.css({
                    '-moz-transform': 'rotate(' + rotateDegrees + 'deg)',
                    '-webkit-transform': 'rotate(' + rotateDegrees + 'deg)',
                    '-o-transform': 'rotate(' + rotateDegrees + 'deg)',
                    '-ms-transform': 'rotate(' + rotateDegrees + 'deg)',
					'-sand-transform': 'rotate(' + rotateDegrees + 'deg)'
                });
            });
        }
    };
});