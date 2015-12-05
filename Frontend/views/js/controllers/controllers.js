angular.module('controller', [])
.controller('MainController', function($scope)
{
  console.log('Hello from Main controller');

  $scope.testClick = function()
  {
    console.log('Clicking button from Main Ctrl.')
  }
})
.controller('ParentController', function($scope, ServiceForUser, $location){
  $scope.token;
  $scope.currentUserID;
  $scope.submitted = false;
  console.log("Hello from ParentController");

  $scope.loginUser = function(){
    console.log("In Login User method.");

    if ($scope.login_form.$valid) {

    ServiceForUser.loginUser($scope.user).success(function(data){
      console.log(data);
      ServiceForUser.setToken(data.token);
      $location.path('/account');
    });
    }
    else {
            $scope.login_form.submitted = true;
            console.log($scope.login_form);
            console.log($scope.submitted);
        }

  }

})
.controller('CreateEventController', function ($window, $scope, ServiceForEvents, ServiceForUser){
  //Retrive token from service.
  $scope.token = ServiceForUser.getToken();
  console.log("Token :" + $scope.token)

  //Create a scope to determine if form has been submitted
  $scope.submitted = false;

  $scope.imageUpload = function(event){
    var file = event.target.files[0];
    var reader = new FileReader();
    reader.onload = $scope.imageIsLoaded;
    reader.readAsDataURL(file);
  }
  $scope.imageIsLoaded = function(e){
    $scope.$apply(function() {
      $scope.step = e.target.result;
      // console.log(e.target.result);
    });
   }

  //Method called when the form is submitted
  $scope.createEvent = function(){

      if($scope.step != null && $scope.event)
      {
        console.log($scope.event.picture);
        $scope.event.picture =  $scope.step.replace('data:image/jpeg;base64,', '');
        console.log($scope.event.picture);
      }


    console.log($scope.event);
      //If the form fields are all valid
    if ($scope.new_event_form.$valid)
    {
      console.log($scope.new_event_form);
      //Call the addEvent method of Event Service to create a new event.
      ServiceForEvents.addEvent($scope.event, $scope.token).success(function (data)
      {
        console.log("Event Created.")
        $window.location.reload();
      });
    }
    else {
        $scope.new_event_form.submitted = true;
    }
  }

})

.controller('UpdateEventController', function ($window, $scope, $stateParams, ServiceForEvents, ServiceForUser){
  $scope.event = [];
  $scope.id = $stateParams.eventID;
  $scope.submitted = false;
  $scope.token = ServiceForUser.getToken();

  console.log($scope.id);
  //Retrieve selected Event info based on the id
  ServiceForEvents.getEventById($scope.id, $scope.token).success(function (data) {
      //Store the event details in the event model
      $scope.event = data;
  });

  console.log($scope.event);

  $scope.updateEvent = function () {
    if($scope.step != null && $scope.event)
        {
          console.log("Bfore" + $scope.event.picture);
          $scope.event.picture =  $scope.step.replace('data:image/jpeg;base64,', '');
          console.log("After" + $scope.event.picture);
        }
    if ($scope.update_event_form.$valid) {
      // Submit as normal
      console.log($scope.update_event_form);
      ServiceForEvents.updateEvent($scope.id, $scope.event, $scope.token).success(function (data)
      {
        console.log($scope.event);
        $window.location.reload();
        //should redirect to events
      });
    } else {
        $scope.update_event_form.submitted = true;
        console.log("Form is not complete.")
    }
  }

  $scope.imageUpload = function(event){
    var file = event.target.files[0];
    var reader = new FileReader();
    reader.onload = $scope.imageIsLoaded;
    reader.readAsDataURL(file);
  }
  $scope.imageIsLoaded = function(e){
    $scope.$apply(function() {
      $scope.step = e.target.result;
      // console.log(e.target.result);
    });
  }

})

.controller('EventListController', function ($scope, ServiceForEvents, ServiceForUser){
  $scope.token = ServiceForUser.getToken();
  console.log($scope.token);

  ServiceForEvents.getAllEvents($scope.token).success(function (data)
  {
      $scope.eventList = data;
      console.log(data);
  }
  );

})

.controller('EventDetailsController', function ($window, $scope, $stateParams, ServiceForEvents, ServiceForUser){
  $scope.id = $stateParams.eventID;
  $scope.token = ServiceForUser.getToken();

  console.log($scope.id);
  ServiceForEvents.getEventById($scope.id, $scope.token).success(function (data)
  {
    console.log(data);
    $scope.event = data;
    $scope.event.thumbnail = 'data:image/jpeg;base64,' + $scope.event.picture;
  }
  );


  //$scope.event.picture = "data:image/jpeg;base64," + $scope.event.picture;

  $scope.deleteEvent = function () {
    $scope.token = ServiceForUser.getToken();

    console.log("about to delete event")
    if (confirm("Are you sure you want to delete the event?") == true)
        ServiceForEvents.deleteEvent($scope.id, $scope.token).success(function (data) {});
    $window.location.reload();
  };

})

//search users
.controller('SearchUserController', function ($scope, ServiceForUser){
  $scope.token = ServiceForUser.getToken();
  console.log($scope.token);

  $scope.searchUser = function () {
    console.log($scope.user.search);
    ServiceForUser.searchUser($scope.user.search, $scope.token).success(function (data)
    {
        $scope.user = data;
        console.log(data);
    }
    );
  }

})

//user profile
.controller('UserProfileController', function ($scope, $stateParams, ServiceForUser){
  $scope.id = $stateParams.userID;
  $scope.token = ServiceForUser.getToken();
  console.log($scope.id);

  ServiceForUser.findUserByID($scope.id, $scope.token).success(function (data)
  {
      $scope.user = data;
      console.log(data);
  }
  );

})




// angular.module('userController', [])
.controller('RegisterUserController', function ($scope, $location, ServiceForUser) {

    //Create a scope to determine if form as been submitted
    $scope.submitted = false;
    //Method that is called when the form is submitted
    $scope.signupForm = function () {
        //If the form is fields are valid
        if ($scope.register_form.$valid) {
            console.log($scope.register_form);
            ServiceForUser.registerUser($scope.user).success(function (data) {
                if(data.error != null){
                    $location.path('/Error');
                }
                else
                {
                    $location.path('/events');
                }
                console.log("Saved User.");
                //Reset values on the page once new user is registered.
                $scope.user.email = "";
                $scope.user.friendlyName = "";
                $scope.user.hashPassword = "";
                $scope.register_form.$setPristine();
                $scope.register_form.submitted = false;
                //$location.path('/Login');
            }
            );

          //If the form is not valid, tell that the form has been submitted to display validations
        } else {
            $scope.register_form.submitted = true;
            console.log($scope.register_form);
            console.log($scope.submitted);
        }
    }

});
