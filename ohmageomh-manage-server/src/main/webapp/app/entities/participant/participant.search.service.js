(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .factory('ParticipantSearch', ParticipantSearch);

    ParticipantSearch.$inject = ['$resource'];

    function ParticipantSearch($resource) {
        var resourceUrl =  'api/_search/participants/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
