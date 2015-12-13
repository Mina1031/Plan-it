//Load datbase connection file
var database = require('../database');

var messages = module.exports;

messages.createMessage = function(req, res, next){
  database.userModel.findOne({token: req.headers["x-access-token"]}, function(err, result){
    database.messagesModel.findOne({
      "EventID": req.params.event
    },function(err, messages){
        if(err)
          console.log(err);
        else{
          var message = new database.messageModel({
            "message" : req.body.message,
            "userID" : result.UserID,
            "friendlyName" : result.friendlyName
          });
          messages.messages.push(message);

          messages.save(function(err, result){
            if(err)
              console.log(err);
            else
              res.sendStatus(201);
          });
        }
    });
  });
}

messages.getAllMessagesSinceID = function(req, res, next){
  database.messagesModel.findOne({"EventID" : req.params.event}, function(err, result){
    if(err)
      console.log(err);
    else{
      database.messagesModel.messageList(parseInt(req.params.msgid), parseInt(req.params.event), function(result2){
        if(err)
          console.log(err);
        else
          res.json(result2);
      });
    }
  });
}
