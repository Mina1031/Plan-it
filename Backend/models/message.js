var mongoose = require('mongoose');

var message = new mongoose.Schema({
    _id: false,
    MessageID: { type : Number},
    message: String,
    userID: Number,
    friendlyName: String,
    time: { type: Date, default: Date.now }
});

module.exports = message;
