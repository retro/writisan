'use strict';

const assert = require('assert');
const app = require('../../../src/app');

describe('bucket service', function() {
  it('registered the buckets service', () => {
    assert.ok(app.service('buckets'));
  });
});
