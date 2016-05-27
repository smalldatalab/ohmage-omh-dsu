(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Study', Study);

    Study.$inject = ['$resource', 'DateUtils', '_'];

    function Study ($resource, DateUtils, _) {
        var resourceUrl =  'api/studies/:id';

        return $resource(resourceUrl, {id: null, participant: null}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                    data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);

                    // Extract unique data types from integrations and assign a color
                    data.dataTypes = _.chain(data.integrations).pluck("dataTypes").flatten().unique().value();
                    var colors = ['info', 'warning', 'success', 'important', 'special', 'inverse'];
                    _.each(data.dataTypes, function(dataType, index) {
                        dataType.color = colors[index % 6];
                    });

                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.startDate = DateUtils.convertLocalDateToServer(data.startDate);
                    data.endDate = DateUtils.convertLocalDateToServer(data.endDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.startDate = DateUtils.convertLocalDateToServer(data.startDate);
                    data.endDate = DateUtils.convertLocalDateToServer(data.endDate);
                    return angular.toJson(data);
                }
            },
            'getParticipants': {
                method: 'GET',
                url: resourceUrl + '/participants',
                params: {id: '@id', size: 5000},
                isArray: true
            },
            'getParticipant': {
                method: 'GET',
                url: resourceUrl + '/participants/:participant',
                params: {id: '@id', participant: '@participant'}
            },
            'getParticipantSummaries': {
                method: 'GET',
                url: resourceUrl + '/participantSummaries',
                params: {id: '@id', size: 5000},
                isArray: true
            }

        });
    }
})();
