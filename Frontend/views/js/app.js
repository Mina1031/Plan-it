// var app = angular.module('planItApp', ['ngRoute']);
var app = angular.module('planItApp', ['ui.router', 'services', 'controller']);

//to route views on single page
app.config(['$stateProvider',
  function ($stateProvider) {
    $stateProvider
        .state('/', {
            url: '',
            views: {
              '': {templateUrl: 'index.html', controller: 'MainController'},
              'generalView': {templateUrl: 'login.html', controller: 'ParentController'}

            }
        })
        .state('signup', {
            url: '/signup',
            views: {
                'generalView': {
                    templateUrl: 'signup.html', controller: 'RegisterUserController'
                }
            }
        })
        .state('login', {
          url: '/login',
          views: {
            'generalView': {
              templateUrl: 'login.html', controller: 'ParentController'
            }
          }
        })
        .state('event', {
            url: '/event',
            views: {
                'generalView': {
                    templateUrl: 'event.html', controller: 'EventListController'
                }
            }
        })
        .state('event.details', {
            url: '/eventinfo/:eventID',
            views: {
                'rightView': {
                    templateUrl: 'event.details.html', controller:'EventDetailsController'
                }
            }
        })
        .state('event.chat', {
            url: '',
            views: {
                'rightView': {
                    templateUrl: 'event.chat.html'
                }
            }
        })
        .state('event.items', {
            url: '',
            views: {
                'rightView': {
                    templateUrl: 'event.items.html'
                }
            }
        })
        .state('event.budget', {
            url: '',
            views: {
                'rightView': {
                    templateUrl: 'event.budget.html'
                }
            }
        })
        .state('event.create', {
            url: '/new',
            views: {
                'rightView': {
                    templateUrl: 'event.create.html', controller: 'CreateEventController'
                }
           }
        })
        .state('event.update', {
            url: '/update/:eventID',
            views: {
                'rightView': {
                    templateUrl: 'event.update.html', controller: 'UpdateEventController'
                }
            }
        })
        .state('friends', {
            url: '/friends',
            views: {
                'generalView': {
                    templateUrl: 'friends.html'
                }
            }
        })
        .state('friends.profile', {
            url: '',
            views: {
                'rightView': {
                    templateUrl: 'friends.profile.html'
                }
            }
        })
        .state('users', {
            url: '/users',
            views: {
                'generalView': {
                    templateUrl: 'users.html', controller: 'SearchUserController'
                }
            }
        })
        .state('users.profile', {
            url: '',
            views: {
                'rightView': {
                    templateUrl: 'users.profile.html', controller:'UserProfileController'
                }
            }
        })
        .state('account', {
            url: '/account',
            views: {
                'generalView': {
                    templateUrl: 'account.html'
                }
            }
        })

    }]);

//to extract socket object to be used in controller
app.factory('socket', function(){
  return io.connect('http://localhost:4030');
});
