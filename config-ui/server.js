
const express = require('express');
const bodyParser = require('body-parser');
const proxy = require('http-proxy-middleware');

const app = express();

const port = 4000;

app.set('json spaces', 2);
app.use(bodyParser.json());



app.get('/', (req, res)=>{
    res.send('hello');
});

app.post('/rest/sec/login', (req, res)=>{

    setTimeout(function(){
        var cred = req.body;
        if(cred.user && cred.pass){
            if(cred.user === 'jhon' && cred.pass === 'doe'){
                res.status(200).end();
                return;
            }
        }
    
        res.status(401).send('Username and password does not match.');
    }, 1000);
});




app.use('/rest/api', proxy({target: 'http://localhost:8080', pathRewrite: {'/rest/api/apps': '/rest/apps'}}));




app.use('/config', express.static('web'));
app.use('/config/', express.static('web'));

app.use('/home', express.static('web/home'));
app.use('/home/', express.static('web/home'));

app.use('/static', express.static('web/static'));
app.listen(port, ()=>{
    console.log('Listening on port: ' + port +'.')
});
