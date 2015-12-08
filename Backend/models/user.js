//Load mongoose model and Schema
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
//Load Bcrypt
var bcrypt = require("bcrypt-nodejs");
var SALT_WORK_FACTOR = 10;
//Load Database Configuration file
var database = require('../database');

//Create the User Schema
var userSchema = new Schema({
    UserID: Number,
    friendlyName: { type: String, required: true, unique: true },
    firstName: {type: String, required: false},
    lastName: {type: String, required: false},
    profilePic: {type: String, required: false},
    email: { type: String, required: true, unique: true },
    hashPassword: { type: String, required: true},
    friendList: [{_id: false, userID: {type: Number, required: true}, isFavorite: {type: Boolean, default: true } }],
    events: [{ _id: false, eventID:Number }],
    token: String
  }
);

// Bcrypt pre-method on save
userSchema.pre('save', function(next) {
	var user = this;
	if(!user.isModified('hashPassword'))
    return next();
	bcrypt.genSalt(SALT_WORK_FACTOR, function(err, salt) {
		if(err)
      return next(err);
		bcrypt.hash(user.hashPassword, salt,null, function(err, hash) {
			if(err)
        return next(err);
			user.hashPassword = hash;
			next();
		});
	});
});

// Compare password method for verification
userSchema.methods.comparePassword = function(candidatePassword, cb) {
	bcrypt.compare(candidatePassword, this.hashPassword, function(err, isMatch) {
		if(err)
      return cb(err);
		cb(null, isMatch);
	});
};

//Export the User Schema to be used by the controller, etc...
module.exports = userSchema;
