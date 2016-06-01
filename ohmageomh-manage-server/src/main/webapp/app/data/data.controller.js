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
        vm.study = Study.get({id: $stateParams.study});
        vm.participants = Study.getParticipants({id: $stateParams.study});
        vm.participant = null;
        vm.dataType = null;
        vm.survey = null;
        vm.surveyResponseKeys = [];

        vm.loadData = loadData;
        vm.handleDataResponse = handleDataResponse;
        vm.loadSurveyData = loadSurveyData;
        vm.handleSurveyDataResponse = handleSurveyDataResponse;
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
                    participant: vm.participant.id,
                    schema_namespace: vm.dataType.schemaNamespace,
                    schema_name: vm.dataType.schemaName,
                    schema_version: vm.dataType.schemaVersion,
                    limit: 5000
                };
                vm.gridOptions.columnDefs = angular.fromJson(vm.dataType.csvMapper);
                vm.gridOptions.data = Data.query(params, function(data){vm.handleDataResponse(data);});
            }
        };

        function handleDataResponse(data) {
            if(data.length == 0){
                AlertService.error("This is no data for that search criteria.")
            }
            else if (data.length == 5000){
                AlertService.error("The maximum number of viewable data points has been returned (5,000). There may be more. Contact the system admin to get more information.")
            }
        }

        function loadSurveyData() {
            vm.gridOptions.data = [];
            vm.gridOptions.columnDefs = [];

            if(vm.participant == null) {
                AlertService.warning("You must select a participant.");
            } else if(vm.survey != null){
                // Get responses for a single survey
                try {
                    var def = angular.fromJson(vm.survey.definition);
                } catch (e) {
                    AlertService.error('There is an error is the definition of the survey "' + vm.survey.name + '".');
                    return;
                }
                var params = {
                    participant: vm.participant.id,
                    schema_namespace: def.schema_id.namespace,
                    schema_name: def.schema_id.name,
                    schema_version: def.schema_id.version,
                    limit: 5000
                };

                Data.query(params, function(data){vm.handleSurveyDataResponse(data, vm.survey);});
            } else {
                // Get responses for all surveys
                _.each(vm.study.surveys, function(survey){
                    try {
                        var def = angular.fromJson(survey.definition);
                    } catch (e) {
                        AlertService.error('There is an error is the definition of the survey "' + survey.name + '".');
                        return;
                    }
                    var params = {
                        participant: vm.participant.id,
                        schema_namespace: def.schema_id.namespace,
                        schema_name: def.schema_id.name,
                        schema_version: def.schema_id.version,
                        limit: 5000
                    };

                    Data.query(params, function(data){vm.handleSurveyDataResponse(data, survey);});
                });
            }
        };

        function handleSurveyDataResponse(data, survey) {
            if(data.length == 0){
                AlertService.info('This are no responses for the survey "' + survey.name + '".')
            }
            else if (data.length == 5000){
                AlertService.error("The maximum number of viewable survey responses has been returned (5,000). There may be more. Contact the system admin to get more information.")
            }

            var columns = [
                    {field: "header.participant_id", displayName: "Participant ID", minWidth: 60},
                    {field: "header.participant_label", displayName: "Participant Label", minWidth: 60},
                    {field: "header.creation_date_time", displayName: "Date", cellFilter: 'date: "yyyy-MM-dd HH:mm"', minWidth: 130},
                    {field: "header.schema_id.namespace", displayName: "Schema Namespace",visible: false},
                    {field: "header.schema_id.name", displayName: "Schema Name", minWidth: 100},
                    {field: "header.schema_id.version", displayName: "Schema Version", visible: false}
                ];

            // Derive the rest of the columns from the responses
            var keys = _.chain(data).map(function(item){return item.body.data;}).map(_.keys).flatten().uniq().value();
            vm.surveyResponseKeys = _.uniq(keys.concat(vm.surveyResponseKeys));

            _.each(vm.surveyResponseKeys, function(key){
                columns.push({field: 'body.data.' + key, displayName: key, minWidth: 100});
            });

            vm.gridOptions.columnDefs = columns;
            vm.gridOptions.data = vm.gridOptions.data.concat(data);

        }

    }
})();
