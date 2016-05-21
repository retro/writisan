'use strict';

const service = require('feathers-mongoose');
const post = require('./post-model');
const hooks = require('./hooks');

const globalHooks = require('../../hooks');

const onlyPostUsers = function(data, connection) {
  let userId = connection.user && connection.user._id;
  
  if (!userId) {
    return false;
  }

  return data.accessedBy.map((uid) => "" + uid).indexOf("" + userId) > -1;
}

module.exports = function() {
  const app = this;

  const options = {
    Model: post,
    paginate: {
      default: 5,
      max: 25
    }
  };

  // Initialize our service with any options it requires
  app.use('/posts', service(options));

  // Get our initialize service to that we can bind hooks
  const postService = app.service('/posts');

  // Set up our before hooks
  postService.before(hooks.before);
  //postService.before(globalHooks.addDelay(2000));

  // Set up our after hooks
  postService.after(hooks.after);
  postService.filter('created', onlyPostUsers)
};
