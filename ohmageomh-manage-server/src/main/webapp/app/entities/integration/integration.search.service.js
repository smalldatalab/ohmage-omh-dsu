(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .factory('IntegrationSearch', IntegrationSearch);

    IntegrationSearch.$inject = ['$resource'];

    function IntegrationSearch($resource) {
        var resourceUrl =  'api/_search/integrations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
