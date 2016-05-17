(function() {
    'use strict';
    angular
        .module('ohmageApp')
        .factory('Data', Data);

    Data.$inject = ['$resource'];

    function Data($resource) {
        var resourceUrl = 'api/dataPoints';

        return $resource(resourceUrl, {}, {
           'query': { method: 'GET', isArray: true}
        });
    }
})();
