'use strict';

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');
const auth = require('feathers-authentication').hooks;
const Remarkable = require('remarkable');
const PostModel = require('../post-model.js');

const SPLITTER = '------<CUT-HERE>------';

const markdownRenderer = function () {
  let md = new Remarkable();
  let splitRules = ['blockquote', 'code', 'fence', 'bullet_list', 'ordered_list', 'paragraph', 'table', 'dl', 'heading'];

  splitRules.map(rule => {
    let ruleName = rule + '_close';
    let oldRule = md.renderer.rules[ruleName];
    md.renderer.rules[ruleName] = function () {
      if (ruleName === 'paragraph_close') {
        let res = oldRule.apply(this, arguments);
        return res.length ? res + SPLITTER : res;
      }

      return oldRule.apply(this, arguments) + SPLITTER;
    };
  });

  return md;
}();

const associateCurrentUser = req => {
  let userId = req.params.user._id;
  return new Promise((resolve, reject) => {
    PostModel.findOne({
      _id: req.id,
      accessedBy: { $elemMatch: { $eq: userId } }
    }).then(doc => {
      if (!doc) {
        PostModel.findByIdAndUpdate(req.id, { $push: { accessedBy: userId } }, { safe: true, upsert: true, new: true }).then(() => {
          resolve(req);
        }, reject);
      } else {
        resolve(req);
      }
    }, reject);
  });
};

exports.before = {
  all: [auth.verifyToken(), auth.populateUser(), auth.restrictToAuthenticated()],
  find: [hooks.disable()],
  get: [associateCurrentUser],
  create: [function (req) {
    console.log('REQ', arguments);
    req.data._id = require('crypto').randomBytes(32).toString('hex');
    req.data.accessedBy = [req.params.user._id];
    req.data.parts = markdownRenderer.render(req.data.text).split(SPLITTER);
  }],
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