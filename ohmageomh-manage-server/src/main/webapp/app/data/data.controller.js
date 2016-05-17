(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataController', DataController);

    DataController.$inject = ['$scope', '$state', '$stateParams', '_', 'Data', 'Study']

    function DataController($scope, $state, $stateParams, _, Data, Study) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.study = Study.get({id: $stateParams.study},
            function(data){
                vm.populateDataTypes()
            });
        vm.participants = Study.getParticipants({id: $stateParams.study});
        vm.dataTypes = [];
        vm.participant = null;
        vm.dataType = null;

        vm.loadData = loadData;
        vm.populateDataTypes = populateDataTypes;
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

        function populateDataTypes() {
            var list = [];
            _.each(vm.study.integrations, function(integration) {
                list.push(_.flatten(integration.dataTypes))
            });
            vm.dataTypes = _.flatten(list);
        }

    }
})();
