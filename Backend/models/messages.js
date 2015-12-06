//Load mongoose model and Schema
var mongoose = require('mongoose');
//Load Database Configuration file
var message = require('./message');

var messageList = new mongoose.Schema({
  EventID: Number,
  messages: [message]
});

module.exports = messageList;
