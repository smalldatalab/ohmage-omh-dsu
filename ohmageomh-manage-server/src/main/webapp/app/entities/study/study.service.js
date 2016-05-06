(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Study', Study);

    Study.$inject = ['$resource'];

    function Study ($resource) {
        var resourceUrl =  'api/studies/:id';

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
