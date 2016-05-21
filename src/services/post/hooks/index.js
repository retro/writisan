'use strict';

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');
const auth = require('feathers-authentication').hooks;
const Remarkable = require('remarkable');
const PostModel = require('../post-model.js');
const postmark = require('postmark');

const SPLITTER = '------<CUT-HERE>------';

const markdownRenderer = (function() {
  let md = new Remarkable();
  let splitRules = [
    'blockquote',
    'code',
    'fence',
    'bullet_list',
    'ordered_list',
    'paragraph',
    'table',
    'dl',
    'heading'
  ];

  splitRules.map((rule) => {
    let ruleName = rule + '_close';
    let oldRule = md.renderer.rules[ruleName];
    md.renderer.rules[ruleName] = function(tokens, idx){
      return oldRule.apply(this, arguments) + (tokens[idx].level === 0 ? SPLITTER : "");
    }
  });

  return md;
})();

const associateCurrentUser = (req) => {
  let userId = req.params.user._id;
  return new Promise((resolve, reject) => {
    PostModel.findOne({
      _id: req.id,
      accessedBy: {$elemMatch: {$eq: userId}}
    }).then((doc) => {
      if (!doc) {
        PostModel.findByIdAndUpdate(
          req.id,
          {$push: {accessedBy: userId}},
          {safe: true, upsert: true, new: true}).then(() => {
            resolve(req);
          }, reject);
      } else {
        resolve(req);
      }
    }, reject);
  });
}

exports.before = {
  all: [
    auth.verifyToken(),
    auth.populateUser(),
    auth.restrictToAuthenticated()
  ],
  find: [hooks.disable()],
  get: [
    associateCurrentUser
  ],
  create: [
    function(req) {
      req.data._id = require('crypto').randomBytes(32).toString('hex');
      req.data.accessedBy = [req.params.user._id];
      req.data.parts = markdownRenderer.render(req.data.text).split(SPLITTER);
    }
  ],
  update: [hooks.disable()],
  patch: [hooks.disable()],
  remove: [hooks.disable()]
};

exports.after = {
  all: [],
  find: [],
  get: [],
  create: [
    function(hook) {
      return new Promise(function(resolve, reject) {
        try {
          let id = hook.result.id;
          let user = hook.params.user;
          let client = new postmark.Client(process.env.POSTMARK_TOKEN)
          let emailData = {
            "From": "become@writisan.com",
            "To": user.google.emails[0].value,
            "Subject": "You have just created a new document on Writisan",
            "HtmlBody": '<p>'+user.google.displayName+', you can access your document <a href="https://app.writisan.com/#!comments/'+id+'">here</a></p>'
          }
          if (process.env.NODE_ENV === 'production') {
            client.sendEmail(emailData, function(error, success) {
              console.log('ERROR SENDING EMAIL', error);
              resolve(hook);
            });
          } else {
            console.log('SENDING EMAIL', emailData);
            resolve(hook);
          }
        } catch (e) {
          console.log('ERROR', e);
          resolve(hook);
        }
      })
    }
  ],
  update: [],
  patch: [],
  remove: []
};
