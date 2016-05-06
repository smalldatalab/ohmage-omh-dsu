(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .factory('SurveySearch', SurveySearch);

    SurveySearch.$inject = ['$resource'];

    function SurveySearch($resource) {
        var resourceUrl =  'api/_search/surveys/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
