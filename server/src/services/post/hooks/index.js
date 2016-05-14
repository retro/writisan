'use strict';

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');
const auth = require('feathers-authentication').hooks;
const Remarkable = require('remarkable');

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
    md.renderer.rules[ruleName] = function(){
      if (ruleName === 'paragraph_close') {
        let res = oldRule.apply(this, arguments);
        return res.length ? res + SPLITTER : res;
      }

      return oldRule.apply(this, arguments) + SPLITTER;
    }
  });

  return md;
})();


exports.before = {
  all: [
    auth.verifyToken(),
    auth.populateUser(),
    auth.restrictToAuthenticated()
  ],
  find: [],
  get: [],
  create: [
    function(req) {
      console.log( markdownRenderer.render(req.data.text))
      req.data.parts = markdownRenderer.render(req.data.text).split(SPLITTER);
    }
],
  update: [],
  patch: [],
  remove: []
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
