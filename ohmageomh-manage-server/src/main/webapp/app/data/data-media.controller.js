(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataMediaController', DataMediaController);

    DataMediaController.$inject = ['$scope', '$rootScope', '$stateParams', '$uibModalInstance', 'Data'];

    function DataMediaController($scope, $rootScope, $stateParams, $uibModalInstance, Data) {
        var vm = this;

        var slides = $scope.slides = [];
        var currIndex = 0;

        $scope.addSlide = function(dataPoint, mediaId) {
            var newWidth = 600 + slides.length + 1;
            slides.push({
                image: 'api/dataPoints/' + dataPoint + '/media/' + mediaId + '/',
                id: currIndex++,
                mediaId: mediaId
            });
        };

        for (var i = 0; i < $stateParams.mediaArray.length; i++) {
            var media = $stateParams.mediaArray[i];
            $scope.addSlide(media.data_point_id, media.media_id);
        }

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

    }
})();
