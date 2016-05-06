(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('SurveyDetailController', SurveyDetailController);

    SurveyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Survey', 'Study'];

    function SurveyDetailController($scope, $rootScope, $stateParams, entity, Survey, Study) {
        var vm = this;
        vm.survey = entity;
        
        var unsubscribe = $rootScope.$on('ohmageApp:surveyUpdate', function(event, result) {
            vm.survey = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
