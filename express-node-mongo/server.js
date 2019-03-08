const express = require('express');
const bodyParser = require('body-parser');
const mongo = require('mongodb');

var app = express();
app.set('json spaces', 2);
app.use(express.static('web'));
app.use(bodyParser.json());

const url = 'mongodb://localhost:27017';
var db;
const client = new mongo.MongoClient(url, { useNewUrlParser: true });
client.connect( err => {
	if(err){
		console.err(err);
		return;
	}
	db = client.db('sakila');
	app.listen(3000,  () => {
		console.log('Example app listening at 3000.');
	});
});

app.get('/rest/customers', (req, res) => {
	db.collection('customers').find().toArray((err, result) => {
		res.json(result);
	});
});

app.get('/rest/customers/:id', (req, res) => {
	var id = req.params.id;
	db.collection('customers').findOne({_id: id}, (err, result) => {
		res.json(result);
	});
});

app.post('/rest/customers', (req, res)=>{

	var body = req.body;
	db.collection('customers').insertOne(body, (err, result) => {
		if(err){
			console.error(err);
			throw err;
		}
		var newId = result.insertedId;

		res.status(201);
		res.location('/rest/customers/' + newId);
		res.send('ok');
		res.end();
	});
});
