(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .factory('_', Underscore);

    Underscore.$inject = ['$window'];

    function Underscore ($window) {
        return $window._;
    }
})();
