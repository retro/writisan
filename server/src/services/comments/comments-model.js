'use strict';

// comments-model.js - A mongoose model
// 
// See http://mongoosejs.com/docs/models.html
// for more of what you can do here.

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const commentsSchema = new Schema({
  postId: String,
  postedById: Schema.Types.ObjectId,
  text: { type: String, required: true },
  idx: Number,
  createdAt: { type: Date, 'default': Date.now },
  updatedAt: { type: Date, 'default': Date.now }
});

const commentsModel = mongoose.model('comments', commentsSchema);

module.exports = commentsModel;
