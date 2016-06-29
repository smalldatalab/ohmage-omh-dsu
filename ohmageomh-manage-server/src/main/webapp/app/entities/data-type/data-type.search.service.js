(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .factory('DataTypeSearch', DataTypeSearch);

    DataTypeSearch.$inject = ['$resource'];

    function DataTypeSearch($resource) {
        var resourceUrl =  'api/_search/data-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
