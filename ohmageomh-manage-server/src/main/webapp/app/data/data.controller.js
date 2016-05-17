(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataController', DataController);

    DataController.$inject = ['$scope', '$state', 'DataType', 'Participant', 'Data']

    function DataController($scope, $state, DataType, Participant, Data) {
        var vm = this;
        vm.dataTypes = DataType.query();
        vm.participants = Participant.query();
        vm.loadData = loadData;
        vm.gridOptions = {
            data: [],
            enableGridMenu: true,
            exporterCsvFilename: 'OhmageDataExport.csv',
            exporterMenuPdf: false,
            enableColumnResize: true,
            onRegisterApi: function(gridApi){
                vm.gridApi = gridApi;
            }
        };

        function loadData() {
            vm.gridOptions.data = Data.query();
            vm.dataType = vm.dataTypes[1];

            vm.gridOptions.columnDefs = angular.fromJson(vm.dataType.csvMapper);
        };
    }
})();
