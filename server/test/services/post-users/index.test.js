'use strict';

const assert = require('assert');
const app = require('../../../src/app');

describe('post-users service', function() {
  it('registered the post-users service', () => {
    assert.ok(app.service('post-users'));
  });
});
