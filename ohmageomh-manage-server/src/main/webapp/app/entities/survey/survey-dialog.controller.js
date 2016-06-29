(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('SurveyDialogController', SurveyDialogController);

    SurveyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Survey', 'Study'];

    function SurveyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Survey, Study) {
        var vm = this;
        vm.survey = entity;
        vm.studies = Study.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:surveyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.survey.id !== null) {
                Survey.update(vm.survey, onSaveSuccess, onSaveError);
            } else {
                Survey.save(vm.survey, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
