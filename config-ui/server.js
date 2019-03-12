
const express = require('express');

const app = express();
app.set('json spaces', 2);
app.use(express.static('web'));
app.use(bodyParser.json());

