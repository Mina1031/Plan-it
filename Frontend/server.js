var express = require('express'),
    app = express(),
    mongoose = require('mongoose'),
    http = require('http').createServer(app),
    io = require('socket.io').listen(http);
var moment = require('moment');
    moment().format();

http.listen(4030, function(){
  console.log('the magic happens on *:4030');
});

app.use(express.static(__dirname+'/views'));
