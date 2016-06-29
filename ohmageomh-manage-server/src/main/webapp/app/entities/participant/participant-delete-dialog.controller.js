(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('ParticipantDeleteController',ParticipantDeleteController);

    ParticipantDeleteController.$inject = ['$uibModalInstance', 'entity', 'Participant'];

    function ParticipantDeleteController($uibModalInstance, entity, Participant) {
        var vm = this;
        vm.participant = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Participant.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
