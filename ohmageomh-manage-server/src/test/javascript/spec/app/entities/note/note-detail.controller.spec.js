'use strict';

describe('Controller Tests', function() {

    describe('Note Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockNote, MockStudy, MockUser, MockParticipant;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockNote = jasmine.createSpy('MockNote');
            MockStudy = jasmine.createSpy('MockStudy');
            MockUser = jasmine.createSpy('MockUser');
            MockParticipant = jasmine.createSpy('MockParticipant');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Note': MockNote,
                'Study': MockStudy,
                'User': MockUser,
                'Participant': MockParticipant
            };
            createController = function() {
                $injector.get('$controller')("NoteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ohmageApp:noteUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
