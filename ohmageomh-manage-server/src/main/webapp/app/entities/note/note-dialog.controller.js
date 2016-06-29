(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('NoteDialogController', NoteDialogController);

    NoteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Note', 'Study', 'User', 'Participant'];

    function NoteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Note, Study, User, Participant) {
        var vm = this;
        vm.note = entity;
        vm.studies = Study.query();
        vm.users = User.query();
        vm.participants = Participant.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:noteUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.note.id !== null) {
                Note.update(vm.note, onSaveSuccess, onSaveError);
            } else {
                Note.save(vm.note, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.date = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
