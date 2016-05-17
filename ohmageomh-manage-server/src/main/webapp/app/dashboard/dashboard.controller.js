(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['$scope', '$state', '$stateParams', 'Study', 'DataType', 'Participant', 'Data']

    function DashboardController($scope, $state, $stateParams, Study, DataType, Participant, Data) {
        var vm = this;
        vm.study = Study.get({id: $stateParams.study});
        vm.dataTypes = DataType.query();
        vm.participants = Participant.query();

    }
})();
