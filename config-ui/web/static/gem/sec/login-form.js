
class LoginForm extends React.Component{

    state = {
        hasEmptyFields: true
    }

    form = null;
    userInput = null;
    passInput = null;
    btn = null;
    btnIconSignIn = null;
    btnIconSync = null;
    btnMsg = null;

    userMsg = null;
    passMsg = null;

    errorHolder = null;

    constructor(props){
        super(props);

        this.html = {};
        this.html.form = React.createRef();
        this.html.userInput = React.createRef();
        this.html.userMsg = React.createRef();
        this.html.passInput = React.createRef();
        this.html.passMsg = React.createRef();
        this.html.errorHolder = React.createRef();
        this.html.btn = React.createRef();
        this.html.btnIconSignIn = React.createRef();
        this.html.btnIconSync = React.createRef();
        this.html.btnMsg = React.createRef();
    }


    handleModification(){
        var user = jQuery.trim(this.userInput.val());
        var pass = this.passInput.val();

        //since this method is beign called in an interval,
        //let's save setSate calls by check the value first.
        if(user == '' || pass == ''){
            if(this.state.hasEmptyFields == false){
                this.setState({hasEmptyFields: true});
            }
            return;
        }

        if(this.state.hasEmptyFields == true){
            this.setState({hasEmptyFields: false});
        }
    }


    handleKeyPress(event){
        var key = event.key;
        var which = event.which;
        if(key == 'Enter' || which == 13){
            this.login();
        }
    }

    onShow(){
        if(localStorage){
            var user = localStorage.getItem('login.user');
            if(user){
                this.userInput.val(user);
                this.passInput.focus();
            }else{
                this.userInput.focus();
            }
        }else{
            this.userInput.focus();
        }
    }

    componentDidMount(){
        this.form = jQuery(this.html.form.current);
        this.userInput = jQuery(this.html.userInput.current);
        this.passInput = jQuery(this.html.passInput.current);
        this.btn = jQuery(this.html.btn.current);
        this.btnIconSignIn = jQuery(this.html.btnIconSignIn.current);
        this.btnIconSync = jQuery(this.html.btnIconSync.current);
        this.btnMsg = jQuery(this.html.btnMsg.current);

        this.userMsg = jQuery(this.html.userMsg.current);
        this.passMsg = jQuery(this.html.passMsg.current);

        this.errorHolder = jQuery(this.html.errorHolder.current);

        var self = this;
        this.form.find('input, button').on('keypress', (event)=>{self.handleKeyPress(event)});
        
        //quick way to monitor the change of the inputs.
        setInterval(function(){
            self.handleModification();
        }, 100);

        var self = this;
        jQuery(document).ready(function(){
            self.onShow();
        });

    }

    processingState(){
        this.userInput.prop('disabled', true);
        this.passInput.prop('disabled', true);

        this.btnIconSignIn.css('display', 'none');
        this.btnIconSync.css('display', 'inline');
        this.btn.prop('disabled', true);
        //this.btnMsg.html('Log in...');
    }

    initalState(){
        this.userInput.prop('disabled', false);
        this.passInput.prop('disabled', false);

        this.btnIconSignIn.css('display', 'inline');
        this.btnIconSync.css('display', 'none');
        this.btn.prop('disabled', false);
        //this.btnMsg.html('Log in');
    }

    validationOff(){
        this.userMsg.css('display', 'none');
        this.passMsg.css('display', 'none');
    }

    packEmail(arg){
        var str;
        if(arg instanceof jQuery){
            str = val();
        }else{
            str = arg;
        }
    
        var lower = str.toLowerCase();
        var ans = '';
        for(var i =0; i < str.length; i++){
            var c = lower[i];
            var o = str[i];
    
            if((c >= 'a' && c <= 'z') || c == '@' || c == '_' || c == '.' || c == ''){
                ans += o;
            }
        }
    
        return ans;
    };

    validate(){


        var user = this.packEmail(this.userInput.val());
        var pass = this.passInput.val();

        this.userInput.val(user);
        this.passInput.val(pass);

        var valid = true;
        var focus = true;
        if(user == null || user == ''){
            this.userMsg.css('display', 'block');
            this.userMsg.html('The user name is required.');
            this.errorHolder.css('display', 'none');
            valid = false;
            focus = false;
            this.userInput.focus();
        }else{
            this.userMsg.css('display', 'none');
        }

        if(pass == null || pass == ''){
            this.passMsg.css('display', 'block');
            this.passMsg.html('The password is required.');
            this.errorHolder.css('display', 'none');
            valid = false;
            if(focus){
                focus = false;
                this.passInput.focus();
            }
        }else{
            this.passMsg.css('display', 'none');
        }

        return valid;
    }

    login(){
        if(this.validate() == false){
            return;
        }
        
        var user = this.userInput.val();
        var pass = this.passInput.val();

        var cred = {user: user, pass: pass};

        this.processingState();
        var self = this;
        rest.post('/rest/gem/sec/login', cred).then(function(){
            self.initalState();
            self.passInput.val('');
            self.errorHolder.css('display', 'none');
            if(localStorage){
                localStorage.setItem('login.user', user);
                localStorage.setItem('login.storageDate', new Date().getTime());
            }

            window.location = '/home';
        }).catch(function(jqXHR, status, error){
            self.passInput.val('');
            self.initalState();
            var msg = 'There was an error in the system. Please try again later.';
            if(jqXHR.status === 401){
                msg = 'User name and password does not match.';
            }
            self.errorHolder.css('display', 'block');
            self.errorHolder.html(msg);

            if(jqXHR.status === 401){
                self.passInput.focus();
            }
        });
    }


    render(){
        return (
            <form onSubmit={()=>{return false}} ref={this.html.form}>
                <div className="form-group">
                    <label style={{width: '100%'}}>
                        User: <input ref={this.html.userInput} 
                                className="form-control" type="text" placeholder="Ex.: jhon"/>
                    </label>
                    <div ref={this.html.userMsg} className="invalid-feedback" style={{display: 'none'}}></div>
                </div>
                <div className="form-group">
                    <label style={{width: '100%'}}>
                        Password:  <input ref={this.html.passInput} className="form-control" type="password"/>
                    </label>
                    <div ref={this.html.passMsg} className="invalid-feedback" style={{display: 'none'}}></div>
                </div>
                <div ref={this.html.errorHolder} className="alert alert-danger" style={{display: 'none'}}></div>
                <div className="form-group">
                        <button ref={this.html.btn} onClick={()=>{this.login()}} 
                            disabled={this.state.hasEmptyFields}
                            type="button" className="btn btn-primary" style={{width: '100%'}}>
                            <span ref={this.html.btnIconSignIn} style={{marginRight: '5px'}}>
                                <i  className="fas fa-sign-in-alt"></i>
                            </span>
                            <span ref={this.html.btnIconSync} style={{marginRight: '5px', display: 'none'}}>
                                <i  className="fas fa-sync-alt fa-spin"></i>
                            </span>
                            <span ref={this.html.btnMsg} >Log in</span>
                        </button>
                </div>
            </form>
        );
    }
}