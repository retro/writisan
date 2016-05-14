'use strict';

const postUsers = require('./post-users');
const comments = require('./comments');
const bucket = require('./bucket');
const post = require('./post');
const authentication = require('./authentication');
const user = require('./user');
const mongoose = require('mongoose');
module.exports = function () {
  const app = this;

  mongoose.connect(app.get('mongodb'));
  mongoose.Promise = global.Promise;

  app.configure(authentication);
  app.configure(user);
  app.configure(post);
  app.configure(bucket);
  app.configure(comments);
  app.configure(postUsers);
};