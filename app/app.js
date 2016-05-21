'use strict';

if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}

const path = require('path');
const serveStatic = require('feathers').static;
const favicon = require('serve-favicon');
const compress = require('compression');
const cors = require('cors');
const feathers = require('feathers');
const configuration = require('feathers-configuration');
const hooks = require('feathers-hooks');
const rest = require('feathers-rest');
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');
const socketio = require('feathers-socketio');
const middleware = require('./middleware');
const services = require('./services');

const app = feathers();

const Bucket = app.service('/buckets/');

app.enable("trust proxy", "loopback");
app.configure(configuration(path.join(__dirname, '..')));

// app.get('/new-bucket', function(req, res) {
//   Bucket.create().then(bucket => {
//     res.redirect(302, '/app/' + bucket._id);
//   }, (err) => {
//     res.status(500).send('ERROR');
//   });
// });

// app.get('/app/:bucketId', function(req, res){
//   res.send('BUCKET ID:' + req.params.bucketId);
// });

app.use(compress()).options('*', cors()).use(cors()).use(favicon(path.join(app.get('public'), 'favicon.ico'))).use('/', serveStatic(app.get('public'))).use(cookieParser()).use(bodyParser.json()).use(bodyParser.urlencoded({ extended: true })).get('/auth/success', function (req, res) {
  res.set('Content-Type', 'text/html');
  res.send('<script>localStorage.setItem("feathers-jwt", "' + req.cookies["feathers-jwt"] + '"); window.close(); </script>');
}).configure(hooks()).configure(rest()).configure(socketio()).configure(services).configure(middleware);

// Set up our own custom redirect route for successful login

module.exports = app;