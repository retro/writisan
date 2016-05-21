'use strict';

const service = require('feathers-mongoose');
const user = require('./user-model');
const hooks = require('./hooks');

const onlySelf = function (data, connection) {
  return data._id === connection.user._id;
};

module.exports = function () {
  const app = this;

  const options = {
    Model: user,
    paginate: {
      default: 5,
      max: 100
    }
  };

  // Initialize our service with any options it requires
  app.use('/users', service(options));

  // Get our initialize service to that we can bind hooks
  const userService = app.service('/users');

  // Set up our before hooks
  userService.before(hooks.before);

  // Set up our after hooks
  userService.after(hooks.after);

  userService.filter({
    created: onlySelf,
    updated: onlySelf,
    removed: onlySelf,
    patched: onlySelf
  });
};