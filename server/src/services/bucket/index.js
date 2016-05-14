'use strict';

const service = require('feathers-mongoose');
const bucket = require('./bucket-model');
const hooks = require('./hooks');

module.exports = function() {
  const app = this;

  const options = {
    Model: bucket,
    paginate: {
      default: 5,
      max: 25
    }
  };

  // Initialize our service with any options it requires
  app.use('/buckets', service(options));

  // Get our initialize service to that we can bind hooks
  const bucketService = app.service('/buckets');

  // Set up our before hooks
  bucketService.before(hooks.before);

  // Set up our after hooks
  bucketService.after(hooks.after);
};
