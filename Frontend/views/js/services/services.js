//Angular service to enable HTTP requests for Events API
angular.module('services', ['ngCookies'])
.service("ServiceForEvents" , ['$http', '$cookies', function ($http, $cookies) {
    this.addEvent = function(data, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/events', method: "POST", data: data, headers: {'x-access-token': token}});
    }
    this.getAllEvents = function(token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/events/user', method: "GET", headers: {'x-access-token': token}});
    }
    this.getEventById = function(id, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id , method: "GET", headers: {'x-access-token': token}});
    }
    this.deleteEvent = function (id, token){
      return $http({ url: 'http://planit.lukefarnell.CA:3000/events/' + id , method: "DELETE", headers: {'x-access-token': token}});
    }
    this.updateEvent = function (id, data, token){
      return $http({ url: 'http://planit.lukefarnell.CA:3000/events/' + id , method: "PUT", data: data, headers: {'x-access-token': token}});
    }
    this.leaveEvent = function (id, token){
      return $http({ url: 'http://planit.lukefarnell.CA:3000/events/' + id + '/leave', method: "POST", headers: {'x-access-token': token}});
    }
    this.setEvent = function(value)
    {
      $cookies.eventID = value;
    }
    this.getEvent = function()
    {
      return $cookies.eventID;
    }
    this.getMembersByEventId = function(id, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/members', method: "GET", headers: {'x-access-token': token}});
    }
    this.answerInvitation = function (id, answer, token){
      return $http({ url: 'http://planit.lukefarnell.CA:3000/events/' + id + '/invite/' + answer, method: "POST", headers: {'x-access-token': token}});
    }

}
])

.service("ServiceForItems" , ['$http', function ($http) {
    this.getListItems = function(id, token){
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/list', method: "GET", headers: {'x-access-token': token}});
    }
    this.createListItem = function(id, data, token){
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/list', method: "POST", data: data, headers: {'x-access-token': token}});
    }
    this.updateItem = function(id, itemId, data, token){
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/list/' + itemId, method: "PUT", data: data, headers: {'x-access-token': token}});
    }
    this.deleteItem = function(id, itemId, token){
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/list/' + itemId, method: "DELETE", headers: {'x-access-token': token}});
    }
    this.claimItem = function(id, itemId, token){
      console.log("claim service called");
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/claim/' + itemId, method: "POST", headers: {'x-access-token': token}});
    }
}
])

.service("ServiceForUser", ['$http', '$cookies', '$location', function ($http, $cookies, $location) {
    var token = "";

    this.inviteEventMember = function(id, friendID, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/events/' + id + '/invite/' + friendID, method: "POST", headers: {'x-access-token': token}});
    }

    this.searchUser = function(data, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/search/' + data + '/friend', method: "GET", data: data, headers: {'x-access-token': token}});
    }
    this.findUserByID = function(data, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/search/' + data + '/user', method: "GET", headers: {'x-access-token': token}});
    }
    this.addNewFriend = function(id, data, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/user/' + id + '/friend', method: "POST", data: data, headers: {'x-access-token': token}});
    }
    this.getAllFriends = function(id, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/user/' + id + '/friend', method: "GET", headers: {'x-access-token': token}});
    }
    this.removeFriend = function(id, friendId, token)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/user/' + id + '/friend/' + friendId, method: "DELETE", headers: {'x-access-token': token}});
    }

    this.registerUser = function(data)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/user', method: "POST", data: data});
    }
    this.loginUser = function(data)
    {
      return $http({url:'http://planit.lukefarnell.CA:3000/login', method: "POST", data: data});
    }
    this.setToken = function(value)
    {
      token = value;
      $cookies.token = value;
    }
    this.getToken = function()
    {
      if($cookies.token != "")
      {
        return $cookies.token;
      }
      else {
        $location.path('/login')
      }
    }
    this.logoutUser = function()
    {
      $cookies.token = "";
      $cookies.userID = "";
    }
    this.setUser = function(value)
    {
      $cookies.userID = value;
    }
    this.getUser = function()
    {
      return $cookies.userID;
    }
}
]);
