(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardParticipantDialogController', DashboardParticipantDialogController);

    DashboardParticipantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Participant', 'Study'];

    function DashboardParticipantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Participant, Study) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.participant = {};

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:participantUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            Study.saveParticipant({
                id: $stateParams.study,
                participant: vm.participant
            }, onSaveSuccess, onSaveError);
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
