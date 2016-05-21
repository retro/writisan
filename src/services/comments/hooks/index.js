'use strict';

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');
const auth = require('feathers-authentication').hooks;

exports.before = {
  all: [
    auth.verifyToken(),
    auth.populateUser(),
    auth.restrictToAuthenticated()
  ],
  find: [
    function(req) {
      if (!req.params.query.postId) {
        return Promise.reject("You can't access comments without postId");
      }
    }
  ],
  get: [hooks.disable()],
  create: [
    auth.associateCurrentUser({ as: 'postedById' })
  ],
  update: [hooks.disable()],
  patch: [hooks.disable()],
  remove: [hooks.disable()]
};

exports.after = {
  all: [],
  find: [],
  get: [],
  create: [],
  update: [],
  patch: [],
  remove: []
};
