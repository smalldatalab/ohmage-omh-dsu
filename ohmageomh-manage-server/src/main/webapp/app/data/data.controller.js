(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataController', DataController);

    DataController.$inject = ['$scope', '$state', '$stateParams', '$timeout', '_', 'AlertService', 'Data', 'Study']

    function DataController($scope, $state, $stateParams, $timeout, _, AlertService, Data, Study) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.participant = null;
        vm.dataType = null;
        vm.surveyResponseKeys = [];
        vm.loadingStatus = {
            active: false,
            loadedCount: 0,
            totalCount: 0
        };

        Study.get({id: $stateParams.study}, function(result) {
            vm.study = result;
            // Change survey response data type name for clarity
            _.each(vm.study.dataTypes, function(dataType) {
                if(dataType.schemaNamespace == "ohmageomh" && dataType.schemaName == "survey-response") {
                    dataType.name = "All Survey Responses";
                }
            })

            // Add surveys as data types
            _.each(vm.study.surveys, function(survey) {
                vm.study.dataTypes = vm.study.dataTypes.concat({
                    id: survey.id,
                    name: "Survey: " + survey.name,
                    isSurvey: true,
                    survey: survey
                })
            })
        });

        Study.getParticipants({id: $stateParams.study}, function(result) {
            // Create proxy field for searching
            vm.participants = _.sortBy(result, function(obj) {
                if(obj.label){
                    obj.searchField = obj.id + " - " + obj.label;
                } else {
                    obj.searchField = obj.id;
                }
                return -obj.id;
            });
            vm.participants.unshift({id: null, searchField: "All participants"});
        });
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

        vm.executeLoad = function() {
            vm.gridOptions.data = [];
            if(vm.dataType == null){
                AlertService.warning("You must select a data type.");
            } else if(vm.participant == null) {
                AlertService.warning("You must select a participant.");
            } else if(vm.dataType.schemaNamespace == "ohmageomh" && vm.dataType.schemaName == "survey-response") {
                // Load all surveys
                if(vm.participant.id != null) {
                    // Single participant
                    vm.gridOptions.data = [];
                    vm.gridOptions.columnDefs = [];
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: vm.study.surveys.length,
                        multiSurvey: true,
                        multiParticipant: false
                    };
                    vm.loadSurveyData(vm.participant, vm.study.surveys[0]);


                } else {
                    // All participants
                    vm.gridOptions.data = [];
                    vm.gridOptions.columnDefs = [];
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: vm.study.surveys.length * (vm.participants.length - 1),
                        multiSurvey: true,
                        multiParticipant: true
                    };
                    vm.loadSurveyData(vm.participants[1], vm.study.surveys[0]);
                }
            } else if(vm.dataType.isSurvey) {
                // Load single survey
                if(vm.participant.id != null) {
                    // Single participant
                    vm.gridOptions.data = [];
                    vm.gridOptions.columnDefs = [];
                    vm.surveyResponseKeys = [];
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: 0
                    };
                    vm.loadSurveyData(vm.participant, vm.dataType.survey);
                } else {
                    // All participants
                    vm.gridOptions.data = [];
                    vm.gridOptions.columnDefs = [];
                    vm.surveyResponseKeys = [];
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: vm.participants.length -1
                    };
                    vm.loadSurveyData(vm.participants[1], vm.dataType.survey);
                }
            } else {
                // Load a data type
                vm.gridOptions.columnDefs = angular.fromJson(vm.dataType.csvMapper);

                if(vm.participant.id != null) {
                    // Single participant
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: 1
                    };
                    vm.loadData(vm.participant, vm.dataType);
                } else {
                    // All participants
                    vm.loadingStatus = {
                        active: true,
                        loadedCount: 0,
                        totalCount: vm.participants.length - 1
                    };
                    vm.loadData(vm.participants[1], vm.dataType);
                }
            }
        };

        vm.loadData = function(participant, dataType) {
            var params = {
                participant: participant.id,
                schema_namespace: dataType.schemaNamespace,
                schema_name: dataType.schemaName,
                schema_version: dataType.schemaVersion,
                limit: 5000
            };

            Data.query(params, function(data){vm.handleDataResponse(data, participant, dataType);}, function(){vm.incrementLoaded();});
        }

        vm.handleDataResponse = function(data, participant, dataType) {
            vm.incrementLoaded();

            if(data.length == 0 && vm.participant.id != null){
                AlertService.error("This is no data for that search criteria.")
            }
            else if (data.length == 5000){
                AlertService.error("The maximum number of viewable data points has been returned (5,000). There may be more. Contact the system admin to get more information.")
            }
            vm.gridOptions.data = vm.gridOptions.data.concat(data);

            if(vm.loadingStatus.loadedCount < vm.loadingStatus.totalCount) {
                // Load next participant
                var index = vm.participants.indexOf(participant);
                vm.loadData(vm.participants[index + 1], dataType);
            }
        }

        vm.loadSurveyData = function(participant, survey) {
            // Get responses for a single survey
            try {
                var def = angular.fromJson(survey.definition);
            } catch (e) {
                AlertService.error('There is an error is the definition of the survey "' + survey.name + '".');
                return;
            }
            var params = {
                participant: participant.id,
                schema_namespace: def.schema_id.namespace,
                schema_name: def.schema_id.name,
                schema_version: def.schema_id.version,
                limit: 5000
            };

            Data.query(params, function(data){vm.handleSurveyDataResponse(data, participant, survey);}, function(){vm.incrementLoaded();});
        };

        vm.incrementLoaded = function() {
            vm.loadingStatus.loadedCount += 1;
            if(vm.loadingStatus.loadedCount >= vm.loadingStatus.totalCount) {
                vm.loadingStatus.active = false;
            }
        }

        vm.handleSurveyDataResponse = function(data, participant, survey) {
            vm.incrementLoaded();

            if(data.length == 0){
                if(vm.participant.id != null){
                    AlertService.info('This are no responses for the survey "' + survey.name + '".')
                }
            }
            else if (data.length == 5000){
                AlertService.error("The maximum number of viewable survey responses has been returned (5,000) for a" +
                    " participant. There may be more. Contact the system admin to get more information.")
            }
            _.each(data, function(it){
                it.body.survey_name = survey.name;
            })

            var columns = [
                    {field: "header.participant_id", displayName: "Participant ID", minWidth: 60},
                    {field: "header.participant_label", displayName: "Participant Label", minWidth: 60},
                    {field: "header.creation_date_time", displayName: "Date", cellFilter: 'date: "yyyy-MM-dd HH:mm"', minWidth: 130},
                    {field: "body.survey_name", displayName: "Survey Name", minWidth: 150},
                    {field: "header.schema_id.namespace", displayName: "Schema Namespace",visible: false},
                    {field: "header.schema_id.name", displayName: "Schema Name", minWidth: 100, visible: false},
                    {field: "header.schema_id.version", displayName: "Schema Version", visible: false},
                    {field: "header.media", displayName: "Photos", minWidth: 60, cellTemplate: '<div class="ui-grid-cell-contents"><a ng-if="grid.getCellValue(row, col).length > 0" ui-sref="data.media({mediaArray: grid.getCellValue(row, col)})">View</a></div>'}
                ];

            // Derive the rest of the columns from the responses
            var keys = _.chain(data).map(function(item){return item.body.data;}).map(_.keys).flatten().uniq().value();
            vm.surveyResponseKeys = _.uniq(keys.concat(vm.surveyResponseKeys));

            _.each(vm.surveyResponseKeys, function(key){
                columns.push({field: 'body.data.' + key, displayName: key, minWidth: 100});
            });

            vm.gridOptions.columnDefs = columns;
            vm.gridOptions.data = vm.gridOptions.data.concat(data);

            // Check if we should load more participants and/or surveys
            if(vm.loadingStatus.loadedCount < vm.loadingStatus.totalCount) {
                if(!vm.loadingStatus.multiSurvey) {
                    // Single survey request, so load next participant
                    var ptIndex = vm.participants.indexOf(participant);
                    vm.loadSurveyData(vm.participants[ptIndex + 1], survey);
                } else {
                    // Multi survey request
                    var surveyIndex = vm.study.surveys.indexOf(survey);
                    if(surveyIndex == vm.study.surveys.length - 1) {
                        // Last survey in list, so check if multi participant, or end
                        if(vm.loadingStatus.multiParticipant){
                            var ptIndex = vm.participants.indexOf(participant);
                            vm.loadSurveyData(vm.participants[ptIndex + 1], vm.study.surveys[0]);
                        }
                    } else {
                        // Move to next survey, same participant
                        vm.loadSurveyData(participant, vm.study.surveys[surveyIndex + 1]);
                    }
                }

            }

        }

    }
})();
