(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Study', Study);

    Study.$inject = ['$resource', 'DateUtils'];

    function Study ($resource, DateUtils) {
        var resourceUrl =  'api/studies/:id';

        return $resource(resourceUrl, {id: null, participant: null}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                    data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);
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
