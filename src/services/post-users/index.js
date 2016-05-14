'use strict';

const hooks = require('./hooks');
const PostModel = require('../post/post-model.js');
const UserModel = require('../user/user-model.js');

class Service {
  constructor(options) {
    this.options = options || {};
  }

  find(params) {
    let postId = params.query.postId;
    if (!postId) {
      return Promise.reject("You can't access post users without postId");
    }

    return new Promise((resolve, reject) => {
      PostModel.findById(postId).then((post) => {
        let accessedBy = post.accessedBy;
        UserModel.find({_id: {$in: accessedBy}}).then((users) => {
          resolve(users.map((u) => {
            return {
              _id: u.id,
              name: u.google.displayName,
              image: u.google.image.url
            }
          }));
        }, reject);
      }, reject);
    });

  }
}

module.exports = function(){
  const app = this;

  // Initialize our service with any options it requires
  app.use('/post-users', new Service());

  // Get our initialize service to that we can bind hooks
  const postUsersService = app.service('/post-users');

  // Set up our before hooks
  postUsersService.before(hooks.before);

  // Set up our after hooks
  postUsersService.after(hooks.after);
};

module.exports.Service = Service;
