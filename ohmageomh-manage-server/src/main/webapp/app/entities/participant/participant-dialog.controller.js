(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('ParticipantDialogController', ParticipantDialogController);

    ParticipantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Participant', 'Study'];

    function ParticipantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Participant, Study) {
        var vm = this;
        vm.participant = entity;
        vm.studies = Study.query({size: 5000});

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
            if (vm.participant.id !== null) {
                Participant.update(vm.participant, onSaveSuccess, onSaveError);
            } else {
                Participant.save(vm.participant, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
