angular.module( 'mymobkit.viewer', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'ngStorage'
]).directive('errSrc', function() {
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
})
.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'viewer', {
    url: '/viewer',
    views: {
      "main": {
        controller: 'ViewerCtrl',
        templateUrl: 'viewer/viewer.tpl.html'
      }
    },
    data:{ pageTitle: 'Viewer' }
  });
}])

.controller( 'ViewerCtrl', ["$rootScope", "$scope", "$window", "$http", "$location", "$interval", "$localStorage", function ViewerCtrl($rootScope, $scope, $window, $http, $location, $interval, $localStorage) {

    console.log("galleryController start");

    var imagesJson = "web/images.json";
    $scope.rowSize = 5; // number of images in a row
    $scope.entryLimit = 10; // max number of items to display in a page
    $scope.currentPage = 1;
    $scope.maxSize = 7; // max number of pagination items
    $scope.defaultImg = "http://rosyjskiwkrakowie.blog.pl/files/2013/08/%D1%81%D0%BF%D0%B0%D1%81%D0%B8%D0%B1%D0%BE11.jpeg";
    var listImg = []; // all images from server
    var searchedImg = [];
    var start = true;
    var timer;
    var storage = $localStorage;

    $http.get(imagesJson).success( function (data) {
        data = $scope.removeDeleted(data, storage);
        listImg = data;
        searchedImg = listImg;
        initPagination(listImg);
    }).error( function (error) {
        console.error(error);
    });

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

    console.log("galleryController end!");

}]
)
;
