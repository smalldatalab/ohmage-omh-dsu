(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Integration', Integration);

    Integration.$inject = ['$resource'];

    function Integration ($resource) {
        var resourceUrl =  'api/integrations/:id';

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
