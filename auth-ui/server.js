
const request       = require('request');
const express       = require('express');
const bodyParser    = require('body-parser');
const uuidv4        = require('uuid/v4');
const session       = require('express-session');
const MemoryStore   = require('memorystore')(session);
const proxy         = require('http-proxy-middleware');

const app           = express();
const port          = 4001;
const backendPort   = 8080;

app.set('x-powered-by', false);
app.set('json spaces', 2);

const SECOND = 1000;
const MINUTE = 60 * SECOND;

//The session must be a top-level middleware
app.use(session({
    key: 'user_sid',
    proxy: false,
    secret: '1Y~{o>7-Kk.jdH$qQY-EP}CSOg+X-{|B#!`=d{hj?u0E+vZlc]Dn~ZNy!];utB:',
    cookie: {
        maxAge: 30 * MINUTE,
        httpOnly: true
    },
    genid: function(req) {
        return uuidv4();
    },
    store: new MemoryStore({
        checkPeriod: 3 * MINUTE
    }),
    resave: false,
    saveUninitialized: true
}));


// This middleware must go inmediatly after the session middleware.
// This middleware will check if user's cookie is still saved in browser
// and user is not set, then automatically log the user out. This usually 
// happens when you stop your express server after login, your cookie still 
// remains saved in the browser.
app.use((req, res, next) => {
    var path = req.path;

    if(path.startsWith('/home')){
        if(req.session && req.session.user){
            next();
            return;
        }
		
		//307 - Temporary Redirect
        res.writeHead(307, {Location: '/'});
        res.end();
        return;
    }

    if (req.cookies && req.cookies.user_sid && !req.session.user) {
        
        res.clearCookie('user_sid');
    }
    next();
});

var options = {};
options.target = 'http://localhost:' + backendPort;
options.changeOrigin = false;
options.preserveHeaderKeyCase = true;
options.proxyTimeout = 30000;
options.timeout = 30000;

app.use('/rest/auth',           proxy('/rest/auth', options));
app.use('/',                    express.static('web'));
app.use('/home',                express.static('web/home'));
app.use('/home/',               express.static('web/home'));
app.use('/static',              express.static('web/static'));
app.use('/static/',             express.static('web/static'));
app.use('/rest/gem/sec/login',  bodyParser.json());


app.post('/rest/gem/sec/login', (req, res)=>{

    setTimeout(function(){
        var cred = req.body;
        var user = cred.user;
        var pass = cred.pass;

        if(user && pass){

            var url = 'http://localhost:' + backendPort + '/rest/auth/sec/match';
            var data = {user: user, pass: pass};

            request.post(url, {json: data}, function(error, res2, body){
                if(typeof(res2) == 'undefined'){
                    res.status(500).send('An error has occurred, please try again later.');
                    return;
                }

                if(res2.statusCode == 200){
                    req.session.user = {name: user};
                    res.status(200).end();
                }else if(res2.statusCode == 401){
                    res.status(401).send('Username and password does not match.');
                }else{
                    res.status(500).send('An error has occurred, please try again later.');
                }
            });
        }else{
            res.status(400).send('Credentials required.');
        }
    }, 1000);
});


app.listen(port, ()=>{
    console.log('Listening on port: ' + port +'.')
});
