'use strict';

describe('Controller Tests', function() {

    describe('Participant Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockParticipant, MockStudy;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockParticipant = jasmine.createSpy('MockParticipant');
            MockStudy = jasmine.createSpy('MockStudy');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Participant': MockParticipant,
                'Study': MockStudy
            };
            createController = function() {
                $injector.get('$controller')("ParticipantDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ohmageApp:participantUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
