(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Organization', Organization);

    Organization.$inject = ['$resource'];

    function Organization ($resource) {
        var resourceUrl =  'api/organizations/:id';

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
