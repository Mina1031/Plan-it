var database = module.exports;

var mongoose = require('mongoose');
var increment = require('mongoose-auto-increment');
var config = require('./config');
var eventjs = require('./models/event');
var listjs = require('./models/list');
var userjs = require('./models/user');
var messagejs = require('./models/message');
var messagesjs = require('./models/messages');

database.createConnection = function() {
  mongoose.connect(config.db);
  var db = mongoose.connection;
  increment.initialize(db);
  this.initListIncrement();
  this.initEventIncrement();
  this.initUserIncrement();
  this.initMessageIncrement();
  db.on('error', console.error.bind(console, 'connection error:'));
  db.on('open', function() {
    console.log("connection made to database");
  });
}

database.initEventIncrement = function() {
  eventjs.plugin(increment.plugin, {
    model: "Event",
    field: 'EventID',
    startAt: 1
  });
}

database.initListIncrement = function() {
  listjs.plugin(increment.plugin, {
    model: "List",
    field: 'ListID',
    startAt: 1
  });
}

//Create autonumber field on UserID field in User model
database.initUserIncrement = function(){
  userjs.plugin(increment.plugin, {
    model: "User",
    field: "UserID",
    startAt: 1
  });
};

database.initMessageIncrement = function(){
  messagejs.plugin(increment.plugin, {
    model: "Message",
    field: "MessageID",
    startAt:1
  });
};

//database.connection = db;
//Create List Model
database.listModel = mongoose.model("List", listjs);
//Create Event Model
database.eventModel = mongoose.model("Event", eventjs);
//Create User model
database.userModel = mongoose.model("User", userjs);
//Create Message model
database.messageModel = mongoose.model("Message", messagejs);
//Create Message model
database.messagesModel = mongoose.model("Messages", messagesjs);

database.eventModel.calculateEst = function(eventid, callback) {
    database.eventModel.aggregate([
      {$match : {EventID:eventid}},
      {
      $unwind: "$itemList"
    }, {
      $group: {
        _id: null,
        estCost: {
          $sum: "$itemList.estCost",
        },
        actCost: {
          $sum: "$itemList.actCost"
        }
      }
    }], function(err, result) {
      if (err) {
        console.log(err);
      }
      console.log("result" + result);
      callback(result);
    });
}

database.messagesModel.messageList = function(msgid, eventid, callback){
    database.messagesModel.aggregate([
      {$match : { EventID : eventid}},
      {$unwind: '$messages'},
      {$match : {'messages.MessageID' : { $gte : msgid}}},
      {$group : {_id:null,
        messages : {
          $push : {
            'MessageID' :'$messages.MessageID',
            'Message' :'$messages.message',
            'userID' : '$messages.userID',
            'time' : '$messages.time'
            }
        }
      }
    }],function(err, result){
      if (err) {
        console.log(err);
        return;
      }
      callback(result);
    });
}
