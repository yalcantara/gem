
const express = require('express');
const bodyParser = require('body-parser');

const app = express();

const port = 4000;

app.set('json spaces', 2);
app.use(bodyParser.json());



app.get('/', (req, res)=>{
    res.send('hello');
});


app.use('/config', express.static('web'));
app.use('/static', express.static('web/static'));
app.listen(port, ()=>{
    console.log('Listening on port: ' + port +'.')
});
