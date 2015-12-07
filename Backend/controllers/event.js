var database = require('../database');
var event = module.exports;

event.createEvent = function(req, res, next) {
  //create the event model from request object
  var event = new database.eventModel({
    "what": req.body.what,
    "why": req.body.why,
    "where": req.body.where,
    "when": req.body.when,
    "endDate": req.body.endDate,
    "fromTime" : req.body.fromTime,
    "toTime" : req.body.toTime,
    "picture": req.body.picture
  });
  //search the user document for the userid based on there token
  database.userModel.findOne({token : req.headers["x-access-token"]}, function(err, token){
    if(err)
      console.log(err);
    else{
      //set the user as the event owner
      event.members.push({UserId: parseInt(token.UserID), friendlyName : token.friendlyName, isAttending: "Owner"});


      //save the event
      event.save(function(err, result) {
        if (err)
          console.log(err);
        else{
          token.events.push({eventID: result.EventID});
          token.save();

          var messages = new database.messagesModel({
            "EventID": result.EventID
          });

          messages.save(function(err, result){
            if(err)
              console.log(err);
          });
          res.sendStatus(201);
        }
      });
    }
  });
}

event.getEventById = function(req, res, next) {
  database.eventModel.findOne({
    "EventID": req.params.id
  }, function(err, event) {
    if (err)
      console.log(err);
    else{
      res.status(200).send(event);
    }
  });
}

event.getAllEvents = function(req, res, next) {
  database.eventModel.find({}, function(err, events) {
    if (err)
      console.log(err);
    else {
      res.status(200).send(events);
    }
  });
}

event.getUsersEvents = function(req, res, next) {
  //search the user document for the userid based on there token
  database.userModel.findOne({token : req.headers["x-access-token"]}, function(err, token){
    if(err)
      console.log("error"  + err);
    else{
      console.log(token);
      database.eventModel.find({"members.UserId": token.UserID}, function(err, events) {
        if (err)
          console.log(err);
        else {
          res.status(200).send(events);
        }
      });
    }
  });
}

event.getMembersByEventId = function(req, res, next){
  database.eventModel.findOne({"EventID" : req.params.id}, function(err, event){
    if(err)
      console.log(err);
    else{
      res.status(200).json(event.members);
    }
  })
}

event.updateEvent = function(req, res, next) {
  database.eventModel.findOne({"EventID" : req.params.id}, function(err, event){
    if(err)
      console.log(err);
    else{
      event.EventID = event.EventID;
      event.what = req.body.what || event.what;
      event.why = req.body.why || event.why;
      event.where = req.body.where || event.where;
      event.when = req.body.when || event.when;
      event.endDate = req.body.endDate || event.endDate;
      event.fromTime = req.body.fromTime || event.fromTime;
      event.toTime = req.body.toTime || event.toTime;
      event.picture = req.body.picture || event.picture;
      event.save(function(err, result){
        if(err)
          console.log(err);
        else{
          res.sendStatus(200);
        }
      });
    }
  });
}

event.deleteEvent = function(req, res, next) {
  database.eventModel.remove({
    "EventID": req.params.id
  }, function (err, event) {
    if (err)
      console.log(err);
    else
      res.sendStatus(200);
  });
}

event.createListItem = function(req, res, next) {
  database.eventModel.findOne({
    "EventID": req.params.id
  }, function(err, event) {
    if (err)
      console.log(err);
    else {
      var list = new database.listModel({
        "item": req.body.item,
        "estCost": req.body.estCost,
        "actCost": req.body.actCost
      });
      event.itemList.push(list);

      database.eventModel.calculateEst(req.params.id, function(result) {
        event.totalEstCost = result.estCost;
        event.totalActCost = result.actCost;
        event.save(function(err) {
          if (err)
            console.log(err);
          else{
            res.sendStatus(201);
          }
        });
      });
    }
  });
}

event.getListItems = function(req, res, next) {
  database.eventModel.findOne({
    "EventID": req.params.id
  }, function(err, events) {
    if (err)
      console.log(err);
    else {
      res.status(200).send(events.itemList);
    }
  });
}

event.claimItem = function(req, res, next){
  //console.log("req params: " + JSON.stringify(req.params, 4, null));
  database.userModel.findOne({token: req.headers["x-access-token"]}, function(err, result){
    if(err)
      console.log(err);
    else{
      console.log(result);
      database.eventModel.update({"itemList.ListID" : req.params.item, "EventID" : req.params.id},
      {$set : { "itemList.$.whoseBringing": result.UserID}},
      function(err, item){
        if(err)
          console.log(err);
        else
          res.sendStatus(201);
      });
    }
  });
}

event.deleteItem = function(req, res, next){
  database.eventModel.findOneAndUpdate({"itemList.ListID" : req.params.item, "EventID" : req.params.id},
  {$pull: {"itemList": {"ListID" : req.params.item}}},
  function(err, item){
    if(err)
      console.log(err);
    else
      console.log(JSON.stringify(item,4,null));
      database.eventModel.calculateEst(req.params.id, function(result) {
        console.log(JSON.stringify(result,4,null));

        item.totalEstCost = result.estCost;
        item.totalActCost = result.actCost;
        item.save(function(err) {
          if (err)
            console.log(err);
          else
            res.sendStatus(201);
        });
      });
  });
}

event.updateItem = function(req, res, next){
  database.eventModel.findOne({"itemList.ListID" : req.params.item, "EventID" : req.params.id}, function(err, result){
    if(err)
      console.log(err);
    else{
      result.itemList[0].ListID = result.itemList[0].ListID;
      result.itemList[0].item = req.body.item || result.itemList[0].item;
      result.itemList[0].actCost = req.body.actCost || result.itemList[0].actCost;
      result.itemList[0].estCost = req.body.estCost || result.itemList[0].estCost;

      database.eventModel.calculateEst(parseInt(req.params.id), function(item) {
        result.totalEstCost = item[0].estCost;
        result.totalActCost = item[0].actCost;

        result.save(function(err, saved){
          if(err)
            console.log(err);
          else{
            res.json(saved);
          }
        });
      });
    }
  });
}

event.inviteFriend = function(req, res, next){
  //console.log("hit")
  database.eventModel.findOne({"EventID" : req.params.id}, function(err, event){
    if(err)
      console.log(err);
    else{
      database.userModel.findOne({"UserID" : req.params.friendId}, function(err, user){
        if(err)
          console.log(err);
        else{
          database.eventModel.findOne({$and:[{"EventID": req.params.id},{"members.UserId": user.UserID}]}, function(err, member){
            if(err)
              console.log(err);
            else{

              if(member){
                res.status(409).send("member is already invited to the event");
              }
              else{
                event.members.push({UserId: user.UserID, friendlyName : user.friendlyName, isAttending: "Invited"});
                event.save(function(err){
                  if(err)
                    console.log(err);
                  else
                    res.sendStatus(200);
                });
              }
            }
          });
        }
      });
    }
  });
}

event.invitation = function(req, res, next){
  database.userModel.findOne({token: req.headers["x-access-token"]}, function(err, user){
    if(err)
      console.log(err);
    else{
      database.eventModel.update({"members.$.UserID" : user.UserId , "EventID" : req.params.id},
       {$set: {"members.$.isAttending" : req.params.answer}},
       function(err, event){
        if(err)
          console.log(err);
        else{
          res.sendStatus(200);
        }
      });
    }
  });
}
