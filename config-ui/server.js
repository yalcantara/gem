
const express = require('express');
const bodyParser = require('body-parser');

const app = express();

const port = 4000;

app.set('json spaces', 2);
app.use(bodyParser.json());



app.get('/', (req, res)=>{
    res.send('hello');
});

app.post('/rest/login', (req, res)=>{

    setTimeout(function(){
        var cred = req.body;
        if(cred.user && cred.pass){
            if(cred.user === 'jhon' && cred.pass === 'doe'){
                res.status(200).end();
                return;
            }
        }
    
        res.status(401).send('Username and password does not match.');
    }, 3000);

    
});


app.use('/config', express.static('web'));
app.use('/config/', express.static('web'));
app.use('/static', express.static('web/static'));
app.listen(port, ()=>{
    console.log('Listening on port: ' + port +'.')
});
