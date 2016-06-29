(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDetailController', StudyDetailController);

    StudyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Study', 'User', 'Survey', 'Integration', 'Participant', 'Organization'];

    function StudyDetailController($scope, $rootScope, $stateParams, entity, Study, User, Survey, Integration, Participant, Organization) {
        var vm = this;
        vm.study = entity;
        
        var unsubscribe = $rootScope.$on('ohmageApp:studyUpdate', function(event, result) {
            vm.study = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
