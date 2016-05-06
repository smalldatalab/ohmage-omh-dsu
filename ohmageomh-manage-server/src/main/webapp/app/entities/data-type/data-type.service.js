(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('DataType', DataType);

    DataType.$inject = ['$resource'];

    function DataType ($resource) {
        var resourceUrl =  'api/data-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
