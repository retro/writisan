'use strict';

const service = require('feathers-mongoose');
const comments = require('./comments-model');
const hooks = require('./hooks');
const PostModel = require('../post/post-model');

const globalHooks = require('../../hooks');

const filterOnlyPostUsers = function(data, connection, hook) {
  let userId = connection.user && connection.user._id;
  
  if (!userId) {
    return false;
  }

  return new Promise((resolve, reject) => {
    PostModel.findOne({_id: data.postId})
      .then(function(post) {
        if (post.accessedBy.map((uid) => "" + uid).indexOf("" + userId) > -1){
          resolve(data, connection, hook);
        } else {
          reject();
        }
      }, reject);
  });
}

module.exports = function() {
  const app = this;

  const options = {
    Model: comments,
    paginate: {
      default: 5,
      max: Infinity
    }
  };

  // Initialize our service with any options it requires
  app.use('/comments', service(options));

  // Get our initialize service to that we can bind hooks
  const commentsService = app.service('/comments');

  // Set up our before hooks
  commentsService.before(hooks.before);
  //commentsService.before(globalHooks.addDelay(2000));

  // Set up our after hooks
  commentsService.after(hooks.after);

  commentsService.filter({
    created: filterOnlyPostUsers
  })
};
