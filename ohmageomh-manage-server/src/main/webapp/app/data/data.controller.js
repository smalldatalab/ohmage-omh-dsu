(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataController', DataController);

    DataController.$inject = ['$scope', '$state', '$stateParams', '_', 'AlertService', 'Data', 'Study']

    function DataController($scope, $state, $stateParams, _, AlertService, Data, Study) {
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
        vm.handleDataResponse = handleDataResponse;
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
            vm.gridOptions.data = [];
            if(vm.dataType == null){
                AlertService.warning("You must select a data type.");
            } else if(vm.participant == null) {
                AlertService.warning("You must select a participant.");
            } else {
                var params = {
                    schema_namespace: vm.dataType.schemaNamespace,
                    schema_name: vm.dataType.schemaName,
                    schema_version: vm.dataType.schemaVersion
                };
                if(vm.participant != null) {
                    params.participant = vm.participant.id;
                }
                vm.gridOptions.columnDefs = angular.fromJson(vm.dataType.csvMapper);
                vm.gridOptions.data = Data.query(params, function(data){vm.handleDataResponse(data);});
            }
        };

        function handleDataResponse(data) {
            if(data.length == 0){
                AlertService.error("This is no data for that search criteria.")
            }
        }

        function populateDataTypes() {
            var list = [];
            _.each(vm.study.integrations, function(integration) {
                list.push(_.flatten(integration.dataTypes))
            });
            vm.dataTypes = _.flatten(list);
        }

    }
})();
