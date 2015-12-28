app.controller("myCtrl", function($scope) {
$http({
  method: 'GET',
  url: 'http://localhost:8989'
}).then(function successCallback(response) {
    // this callback will be called asynchronously
    // when the response is available
  }, function errorCallback(response) {
    // called asynchronously if an error occurs
    // or server returns response with an error status.
  });
});