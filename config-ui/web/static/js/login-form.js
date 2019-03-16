
class LoginForm extends React.Component{


    userInput = null;
    passInput = null;
    btn = null;
    btnMsg = null;

    userMsg = null;
    passMsg = null;

    errorHolder = null;

    constructor(props){
        super(props);

        this.html = {};
        this.html.userInputRef = React.createRef();
        this.html.userMsgRef = React.createRef();
        this.html.passInputRef = React.createRef();
        this.html.passMsgRef = React.createRef();
        this.html.errorHolderRef = React.createRef();
        this.html.btnRef = React.createRef();
        this.html.btnMsgRef = React.createRef();
    }

    handleKeyPress(event){
        var key = event.key;
        var which = event.which;
        if(which == 'Enter' || which == 13){
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
        this.userInput = jQuery(this.html.userInputRef.current);
        this.passInput = jQuery(this.html.passInputRef.current);
        this.btn = jQuery(this.html.btnRef.current);
        this.btnMsg = jQuery(this.html.btnMsgRef.current);

        this.userMsg = jQuery(this.html.userMsgRef.current);
        this.passMsg = jQuery(this.html.passMsgRef.current);

        this.errorHolder = jQuery(this.html.errorHolderRef.current);

        var self = this;
        this.userInput.on('keypress', (event)=>{self.handleKeyPress(event)});
        this.passInput.on('keypress', (event)=>{self.handleKeyPress(event)});
        this.btn.on('keypress', (event)=>{self.handleKeyPress(event)});

        var self = this;
        jQuery(document).ready(function(){
            self.onShow();
        });

    }

    processingState(){
        this.userInput.prop('disabled', true);
        this.passInput.prop('disabled', true);

        this.btn.prop('disabled', true);
        this.btnMsg.html('Log in...');
    }

    initalState(){
        this.userInput.prop('disabled', false);
        this.passInput.prop('disabled', false);

        this.btn.prop('disabled', false);
        this.btnMsg.html('Log in');
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
        var pass = jQuery.trim(this.passInput.val());

        this.userInput.val(user);
        this.passInput.val(pass);

        var valid = true;
        var focus = true;
        if(user == null || user == ''){
            this.userMsg.css('display', 'block');
            this.userMsg.html('The user name is required.');
            valid = false;
            focus = false;
            this.userInput.focus();
        }

        if(pass == null || pass == ''){
            this.passMsg.css('display', 'block');
            this.passMsg.html('The password is required.');
            valid = false;
            if(focus){
                focus = false;
                this.passInput.focus();
            }
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
        rest.post('/rest/sec/login', cred).then(function(){
            self.initalState();
            self.passInput.val('');
            self.errorHolder.css('display', 'none');
            if(localStorage){
                localStorage.setItem('login.user', user);
                localStorage.setItem('login.storageDate', new Date().getTime());
            }

            window.location = '/home';
        }).catch(function(jqXHR, status, error){
            self.initalState();
            var msg = 'Error while log in to the system. Please try again later.';
            if(jqXHR.status === 401){
                msg = 'User name and password does not match.';
            }
            self.errorHolder.css('display', 'block');
            self.errorHolder.html(msg);

            if(jqXHR.status === 401){
                self.passInput.val('');
                self.passInput.focus();
            }
        });
    }


    render(){
        return (
            <form onSubmit={()=>{return false}}>
                <div className="form-group">
                    <label style={{width: '100%'}}>
                        User: <input ref={this.html.userInputRef} 
                                className="form-control" type="text" placeholder="Ex.: jhon"/>
                    </label>
                    <div ref={this.html.userMsgRef} className="invalid-feedback" style={{display: 'none'}}></div>
                </div>
                <div className="form-group">
                    <label style={{width: '100%'}}>
                        Password:  <input ref={this.html.passInputRef} className="form-control" type="password"/>
                    </label>
                    <div ref={this.html.passMsgRef} className="invalid-feedback" style={{display: 'none'}}></div>
                </div>
                <div ref={this.html.errorHolderRef} className="alert alert-danger" style={{display: 'none'}}></div>
                <div className="form-group">
                        <button ref={this.html.btnRef} onClick={()=>{this.login()}} 
                            type="button" className="btn btn-primary" style={{width: '100%'}}>
                            <i className="fas fa-sign-in-alt" style={{marginRight: '5px'}}></i>
                            <span ref={this.html.btnMsgRef}>Log in</span>
                        </button>
                </div>
            </form>
        );
    }
}