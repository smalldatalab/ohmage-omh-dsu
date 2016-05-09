(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDialogController', StudyDialogController);

    StudyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Study', 'User', 'Survey', 'Integration', 'Participant', 'Organization'];

    function StudyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Study, User, Survey, Integration, Participant, Organization) {
        var vm = this;
        vm.study = entity;
        vm.users = User.query();
        vm.surveys = Survey.query();
        vm.integrations = Integration.query();
        vm.participants = Participant.query();
        vm.organizations = Organization.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:studyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.study.id !== null) {
                Study.update(vm.study, onSaveSuccess, onSaveError);
            } else {
                Study.save(vm.study, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
