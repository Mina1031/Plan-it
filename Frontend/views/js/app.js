// var app = angular.module('planItApp', ['ngRoute']);
var app = angular.module('planItApp', ['ui.router', 'services', 'controller', 'angularMoment']);

app.constant('angularMomentConfig', {
  preprocess: 'utc',
  timezone: 'Europe/Berlin'
});

//to route views on single page
app.config(['$stateProvider','$urlRouterProvider',
  function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
        .state('/', {
            url: '/',
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
                    templateUrl: 'event.details.html', controller: 'EventDetailsController'
                }
            }
        })
        .state('event.chat', {
            url: '/chat',
            views: {
                'rightView': {
                    templateUrl: 'event.chat.html', controller: 'EventChatController'
                }
            }
        })
        .state('event.items', {
            url: '/eventitems/:eventID',
            views: {
                'rightView': {
                    templateUrl: 'event.items.html', controller: 'ItemListController'
                }
            }
        })
        .state('event.budget', {
            url: '/budget',
            views: {
                'rightView': {
                    templateUrl: 'event.budget.html', controller: 'EventBudgetController'
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
        .state('event.addfriend', {
            url: '/addfriend/:eventID',
            views: {
                'rightView': {
                    templateUrl: 'event.addfriend.html', controller: 'EventInviteFriendController'
                }
            }
        })
        .state('friends', {
            url: '/friends',
            views: {
                'generalView': {
                    templateUrl: 'friends.html', controller: 'ViewFriendsController'
                }
            }
        })
        .state('friends.profile', {
            url: '/profile/:userID',
            views: {
                'rightView': {
                    templateUrl: 'friends.profile.html', controller: 'UserProfileController'
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
            url: '/profile/:userID',
            views: {
                'rightView': {
                    templateUrl: 'users.profile.html', controller: 'UserProfileController'
                }
            }
        })
        .state('account', {
            url: '/account',
            views: {
                'generalView': {
                    templateUrl: 'account.html', controller: 'AccountController'
                }
            }
        })

    }]);

//to extract socket object to be used in controller
app.factory('socket', function(){
  return io.connect('http://localhost:4030');
});
